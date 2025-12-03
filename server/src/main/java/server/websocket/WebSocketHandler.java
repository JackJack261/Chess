package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.UserGameCommand;
import io.javalin.websocket.WsErrorContext;

import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    public void onConnect(WsContext ctx) {
        System.out.println("New WebSocket connection: " + ctx.sessionId());
    }

    public void onClose(WsContext ctx) {
        System.out.println("Closed connection: " + ctx.sessionId());
        connections.remove(ctx);
    }

    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        System.out.println("Received: " + message);

        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            // Connect
            case CONNECT -> {
                // handleConnect(ctx, command);
            }

            // Make Move
            case MAKE_MOVE -> {
                // handleMakeMove(ctx, command);
            }

            // Leave

            // Resign

        }
    }

    public void onError(WsErrorContext ctx) {
        System.out.println("WebSocket error: " + ctx.error());
    }
}