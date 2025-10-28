package server;

import handler.GameController;
import io.javalin.*;

public class Server {

    private final Javalin javalin;
    private final GameController gameController = new GameController();


    public Server() {


        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        // Test 'hello world' endpoint
        javalin.get("/hello", ctx -> ctx.result("Hello world!"));

        // Clear database
        javalin.delete("/db", gameController::deleteDb);

        javalin.post("/game", gameController::createGame);
        javalin.get("/game", gameController::listGames);
        javalin.post("/user", gameController::registerUser);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
