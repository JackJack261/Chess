package handler;

import io.javalin.http.Context;

public class GameController {

    // Example endpoint method
    public void createGame(Context ctx) {
        // For now, just respond to test wiring
        ctx.result("Game created!");
    }

    public void listGames(Context ctx) {
        //example list
        ctx.result("Here is a list of all games:");
    }


}