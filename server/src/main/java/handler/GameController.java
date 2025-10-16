package handler;

import requestsAndResults.*;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.Service;

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
        RegisterResult registerResult = service.register(registerRequest);

        ctx.json(new Gson().toJson(registerResult));

    }

}