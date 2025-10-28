package service;

import dataaccess.*;
import models.AuthData;
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



    public DeleteResult deleteDatabase(DeleteRequest deleteRequest) {

        userDAO.removeAll();
        authDAO.removeAll();
        gameDAO.removeAll();

        return new DeleteResult();
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
