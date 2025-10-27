package handler;

import requestsAndResults.*;
import exceptions.*;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.Service;

import java.util.Map;

public class GameController {


    private final Service service = new Service();


    public void createGame(Context ctx) {
        //example game created
        ctx.result("Game created!");
    }

    public void listGames(Context ctx) {
        //example list
        ctx.result("Here is a list of all games:");
    }

    public void registerUser(Context ctx) {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);

        try {
            // 1. Attempt to register the user
            RegisterResult registerResult = service.register(registerRequest);

            // 2. If successful, set status to 200/201 and return the result
            ctx.status(200);
            ctx.json(new Gson().toJson(registerResult));

        } catch (AlreadyTakenException e) {
            // 3. CATCH the specific exception
            ctx.status(403);

            // 4. Create and return a JSON error body with the exception message
            Map<String, String> errorResponse = Map.of(
                    "message", "Error: " + e.getMessage()
            );
            ctx.json(new Gson().toJson(errorResponse));

        } catch (Exception e) {
            // 5. Catch any other unexpected exceptions (good practice)
            ctx.status(500); // Internal Server Error

            Map<String, String> errorResponse = Map.of(
                    "message", "Error: An internal server error occurred."
            );
            ctx.json(new Gson().toJson(errorResponse));
        }
    }
}

