package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.sql.AuthSQLDAO;
import dataaccess.sql.UserSQLDAO;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requestsandresults.*;
import exceptions.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Service {


//    UserDAO userDAO;
//    AuthDAO authDAO;
    GameDAO gameDAO;

    // SQL DAOs
        UserSQLDAO userDAO;
        AuthSQLDAO authDAO;



    public Service() {
//        userDAO = new UserDAO();
//        authDAO = new AuthDAO();
        gameDAO = new GameDAO();

        // SQL DAOs
        userDAO = new UserSQLDAO();
        authDAO = new AuthSQLDAO();


        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            System.out.println("Database cannot be created.");
        }
    }



    private String hashUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }


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

//            String hashedPassword = hashUserPassword(password);

            UserData userData = new UserData(username, password, registerRequest.email());
            AuthData authData = new AuthData(username, authToken);

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

        // This is OG CODE
//        UserData storedUser = userDAO.getUser(username);

        // NEW CODE

        UserData storedUser = null;
        try {
            storedUser = userDAO.getUser(username);

        } catch (DataAccessException e) {
            System.out.println("User Was Not Retrieved.");
        }

//        String hashedDatabasePassword = userDAO.getUser(username).password();


        if (storedUser != null && password.equals(storedUser.password())) {
            // login user
            String authToken = generateToken();
            AuthData authData = new AuthData(loginRequest.username(), authToken);


            // OLD CODE
//            authDAO.createAuth(authData);

            try {
                authDAO.createAuth(authData);
            } catch (DataAccessException e) {
                System.out.println("Unable to create auth");
            }

            return new LoginResult(loginRequest.username(), authToken);
        } else {
            throw new IncorrectLoginException("Incorrect username or password.");
        }

    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws IncorrectAuthTokenException, SQLException, DataAccessException {
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


    public CreateResult createGame(CreateRequest createRequest) throws IncorrectAuthTokenException, AlreadyExistsException, InvalidNameException, SQLException, DataAccessException {
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
            GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
            gameDAO.createGame(gameData);

            return new CreateResult(gameID);
        }
    }

    public ListResult list(ListRequest listRequest) throws SQLException, DataAccessException {
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


    public JoinResult join(JoinRequest joinRequest) throws SQLException, DataAccessException {
        String authToken = joinRequest.authToken();
        String playerColor = joinRequest.playerColor();
        int gameID = joinRequest.gameID();

        GameData gameData = gameDAO.getGameByID(gameID);


        if (gameDAO.getGameByID(gameID) == null || gameID == 0) {
            throw new DoesntExistException("Game '" + gameID + "' Doesn't Exist.");
        }
        else if (authToken == null || authDAO.getAuth(authToken) == null) {
            // no authToken or incorrect authToken
            throw new IncorrectAuthTokenException("Invalid Auth Token.");
        }
        else if (playerColor == null || playerColor.isEmpty()) {
            // no playerColor
            throw new DoesntExistException("Player Color Cannot Be Null.");
        }

        else if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            throw new DoesntExistException("Player color must be 'WHITE' or 'BLACK'. Received: " + playerColor);
        }


        else if ((playerColor.equals("WHITE") && gameData.whiteUsername() != null) || (playerColor.equals("BLACK") && gameData.blackUsername() != null)) {
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

    public DeleteResult deleteDatabase(DeleteRequest deleteRequest) throws DataAccessException {

        // OG CODE
//        userDAO.removeAll();


        // NEW CODE

        try {
            userDAO.removeAll();
        } catch (DataAccessException e) {
            System.out.println("Could Not Delete User DB");
        }

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

}
