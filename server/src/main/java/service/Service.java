package service;

import chess.ChessGame;
import dataaccess.*;
import models.AuthData;
import models.GameData;
import models.UserData;
import requestsAndResults.*;
import exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

public class Service {


    UserDAO userDAO = new UserDAO();
    // do same with auth and game
    AuthDAO authDAO = new AuthDAO();
    GameDAO gameDAO = new GameDAO();



    public RegisterResult register(RegisterRequest registerRequest) throws Exception {

        // getUser(username)
        String username = registerRequest.username();
        String password = registerRequest.password();

        if (password == null) {
            throw new Exception("Password cannot be empty.");
        }

        else if (userDAO.getUser(username) == null) {
            //available
            String authToken = generateToken();

            UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthData authData = new AuthData(registerRequest.username(), authToken);

            userDAO.createUser(userData);

            authDAO.createAuth(authData);

            return new RegisterResult(registerRequest.username(), authToken);


        }
        else {
            throw new AlreadyTakenException("The username: '" + username + "' is already taken.");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws IncorrectLoginException, BadRequestException {

        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BadRequestException("missing username or password");
        }

        UserData storedUser = userDAO.getUser(username);


        if (storedUser != null && password.equals(storedUser.password())) {
            // login user
            String authToken = generateToken();
            AuthData authData = new AuthData(loginRequest.username(), authToken);

            authDAO.createAuth(authData);

            return new LoginResult(loginRequest.username(), authToken);
        }
        else {
            throw new IncorrectLoginException("Incorrect username or password.");
        }

    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws IncorrectAuthTokenException {
        String authToken = logoutRequest.authToken();

        if (authToken != null && authDAO.getAuth(authToken) != null) {
            // logout
            authDAO.deleteAuth(authToken);

            return new LogoutResult();

        }
        else {
            throw new IncorrectAuthTokenException("Invalid Auth Token");
        }

    }


    public CreateResult createGame(CreateRequest createRequest) throws IncorrectAuthTokenException, AlreadyExistsException, InvalidNameException{
        String authToken = createRequest.authToken();
        String gameName = createRequest.gameName();

        if (authToken == null || authDAO.getAuth(authToken) == null) {
            // no authToken or incorrect authToken
            throw new IncorrectAuthTokenException("Invalid Auth Token");
        }
        else if (gameName == null) {
            throw new InvalidNameException("Invalid Game Name");
        }
        else if (gameDAO.getGame(gameName) != null) {
            throw new AlreadyExistsException("Game '" + gameName + "' already exists.");
        }
        else {
            int gameID = generateID();
//            String whiteUsername = authDAO.getAuth(authToken).username();
            GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
            gameDAO.createGame(gameData);

            return new CreateResult(gameID);
        }
    }

    public ListResult list(ListRequest listRequest) {
        String authToken = listRequest.authToken();

        if (authToken != null && authDAO.getAuth(authToken) != null) {
            // list games
            HashMap<String, GameData> allListedGames = gameDAO.listGames();

            List<GameInfo> gameInfoList = allListedGames.values().stream()
                    .map(gameData -> new GameInfo(
                            gameData.gameID(),
                            gameData.whiteUsername(),
                            gameData.blackUsername(),
                            gameData.gameName()
                    )).collect(Collectors.toList());
            return new ListResult(gameInfoList);
        }
        else {
            throw new IncorrectAuthTokenException("Invalid Auth Token");
        }
    }


    public JoinResult join(JoinRequest joinRequest) {
        String authToken = joinRequest.authToken();
        String playerColor = joinRequest.playerColor();
        int gameID = joinRequest.gameID();

        GameData gameData = gameDAO.getGameByID(gameID);


        // DEBUG
//        System.out.println("DEBUG: playerColor = " + playerColor);
//        boolean nullTeamColor = playerColor == null;
//        boolean takenTeamColor = gameData.blackUsername() != null;

//        System.out.println("DEBUG: playerColor equals null? " + nullTeamColor );
//        System.out.println("DEBUG: gameData.blackUsername() = " + gameData.blackUsername());
//        System.out.println("DEBUG: Color already taken? " + takenTeamColor);


        if (gameDAO.getGameByID(gameID) == null || gameID == 0) {
            throw new DoesntExistException("Game '" + gameID + "' Doesn't Exist.");
        }
        else if (authToken == null || authDAO.getAuth(authToken) == null) {
            // no authToken or incorrect authToken
            throw new IncorrectAuthTokenException("Invalid Auth Token.");
        }
        else if (playerColor == null || playerColor.isEmpty()) {
            // no playerColor
            System.out.println("DEBUG: playerColor Null Exception taken. playerColor = " + playerColor);
            throw new DoesntExistException("Player Color Cannot Be Null.");
        }

        else if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            throw new DoesntExistException("Player color must be 'WHITE' or 'BLACK'. Received: " + playerColor);
        }


        else if ((playerColor.equals("WHITE") && gameData.whiteUsername() != null) || (playerColor.equals("BLACK") && gameData.blackUsername() != null)) {
            System.out.println("DEBUG: Exception Taken. playerColor = " + playerColor);
            throw new AlreadyExistsException("Player Color '" + playerColor + "' Already Taken");
        }
        else {
            // update game
//            int gameId = gameData.gameID();
            String gameName = gameData.gameName();
            String username = authDAO.getAuth(authToken).username();

            //update black user
            if (playerColor.equals("BLACK")) {
                String whiteUsername = gameData.whiteUsername();

                System.out.println("DEBUG: update Black taken. whiteUsername = " + whiteUsername);
                System.out.println("DEBUG: Username = " + username);

                GameData updatedGame = new GameData(gameID, whiteUsername, username, gameName, gameData.game());
                gameDAO.updateGame(gameName, updatedGame);

            }

            // update white user
            else {
                String blackUsername = gameData.blackUsername();
                GameData updatedGame = new GameData(gameID, username, blackUsername, gameName, gameData.game());
                gameDAO.updateGame(gameName, updatedGame);

            }

            return new JoinResult();
        }


    }



    public DeleteResult deleteDatabase(DeleteRequest deleteRequest) {

        userDAO.removeAll();
        authDAO.removeAll();
        gameDAO.removeAll();

        return new DeleteResult();
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


    private static int idCounter = 1;

    public static synchronized int generateID()
    {
        return idCounter++;
    }



//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
