package service;

import requestsAndResults.RegisterRequest;
import requestsAndResults.RegisterResult;

public class Service {
    public RegisterResult register(RegisterRequest registerRequest) {

        // getUser(username)

        // if user exists, throw AlreadyTakenException

        // if not, call
        // createUser(userData)

        // then createAuth(authData)

        // return username and authToken

        return new RegisterResult(registerRequest.username(), registerRequest.password());
    }
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
