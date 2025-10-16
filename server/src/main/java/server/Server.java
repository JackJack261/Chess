package server;

import handler.GameController;
import handler.UserController;
import io.javalin.*;

public class Server {

    private final Javalin javalin;
    private final GameController gameController = new GameController();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.get("/hello", ctx -> ctx.result("Hello world!"));
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
