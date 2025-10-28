package handler;

import requestsAndResults.*;
import exceptions.*;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.Service;

import java.util.Map;

public class GameController {


    private final Service service = new Service();


    public void listGames(Context ctx) {
        //example list
        ctx.result("Here is a list of all games:");
    }

    public void registerUser(Context ctx) {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);

        try {
            RegisterResult registerResult = service.register(registerRequest);

            ctx.status(200);
            ctx.json(new Gson().toJson(registerResult));

        } catch (AlreadyTakenException e) {

            ctx.status(403);

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));

        } catch (Exception e) {

            ctx.status(400);

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: An internal server error occurred."
            );
            ctx.json(new Gson().toJson(errorResponse));
        }
    }


    // Login User
    public void loginUser(Context ctx) {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);

        try {
            LoginResult loginResult = service.login(loginRequest);

            ctx.status(200);
            ctx.json(new Gson().toJson(loginResult));
        } catch (IncorrectLoginException e) {
            ctx.status(401);

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));
        } catch (BadRequestException e) {
            ctx.status(400);

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: An internal server error occurred."
            );
            ctx.json(new Gson().toJson(errorResponse));
        }
    }

    // Logout User
    public void logoutUser(Context ctx) {
        String authToken = ctx.header("Authorization");

        if (authToken == null) {
            ctx.status(401); // Unauthorized
            ctx.json(Map.of("message", "Error: Missing authentication token."));
            return;
        }

        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        try {
            LogoutResult logoutResult = service.logout(logoutRequest);

            ctx.status(200);
            ctx.json(new Gson().toJson(logoutResult));

        } catch (IncorrectAuthTokenException e) {
            ctx.status(401);

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));
        }


    }


    // Create Game
    public void createGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        record CreateRequestBody(String gameName) {}

        CreateRequestBody body = new Gson().fromJson(ctx.body(), CreateRequestBody.class);
        String gameName = body.gameName();

        CreateRequest createRequest = new CreateRequest(authToken, gameName);

        try {
            CreateResult createResult = service.createGame(createRequest);

            ctx.status(200);
            ctx.json(new Gson().toJson(createResult));
        } catch (IncorrectAuthTokenException e) {
            ctx.status(401);
            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));

        } catch (AlreadyExistsException e) {
            ctx.status(400);
            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));

        } catch (InvalidNameException e) {
            ctx.status(400);

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));
        }

    }


    // Delete Database
    public void deleteDb(Context ctx) {
        DeleteRequest deleteRequest = new Gson().fromJson(ctx.body(), DeleteRequest.class);
        try {
            DeleteResult deleteResult = service.deleteDatabase(deleteRequest);

            ctx.status(200);
            ctx.json(new Gson().toJson(deleteResult));
        }
        catch (Exception e){
            ctx.status(500);
            Map<String, String> errorResponse = Map.of(
                    "message", "Error: An internal server error occurred."
            );
            ctx.json(new Gson().toJson(errorResponse));
        }
    }

}

