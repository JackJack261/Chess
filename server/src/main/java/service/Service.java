package service;

import dataaccess.*;
import models.AuthData;
import models.UserData;
import requestsAndResults.*;

import java.util.UUID;

public class Service {


    UserDAO userDAO = new UserDAO();
    // do same with auth and game
    AuthDAO authDAO = new AuthDAO();
    GameDAO gameDAO = new GameDAO();



    public RegisterResult register(RegisterRequest registerRequest) {

        // getUser(username)
        String username = registerRequest.username();


        if (userDAO.getUser(username) != null) {
            // TODO: make this in its own package
//            throw AlreadyTakenException;
        }
        else {
            //available
            String authToken = generateToken();

            UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthData authData = new AuthData(registerRequest.username(), authToken);

            userDAO.createUser(userData);

            authDAO.createAuth(authData);

            return new RegisterResult(registerRequest.username(), authToken);
        }
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
