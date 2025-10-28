package service;

import chess.ChessGame;
import dataaccess.*;
import models.AuthData;
import models.GameData;
import models.UserData;
import requestsAndResults.*;
import exceptions.*;

import java.util.UUID;

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
            String whiteUsername = authDAO.getAuth(authToken).username();
            GameData gameData = new GameData(gameID, whiteUsername, null, gameName, new ChessGame());
            gameDAO.createGame(gameData);

            return new CreateResult(gameID);
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
