package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.IAuthDAO;
import dataaccess.IGameDAO;
import dataaccess.sql.AuthSQLDAO;
import dataaccess.sql.GameSQLDAO;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import models.AuthData;
import models.GameData;
import websocket.commands.UserGameCommand;
import io.javalin.websocket.WsErrorContext;
import websocket.messages.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final IAuthDAO authDAO = new AuthSQLDAO();
    private final IGameDAO gameDAO = new GameSQLDAO();

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


    private void handleConnect(WsContext ctx, UserGameCommand command) throws IOException, DataAccessException {

        AuthData auth = authDAO.getAuth(command.getAuthToken());
        String username = auth.username();
        int gameID = command.getGameID();

        // Might need to send ErrorMessage if we need to check for invalid auth token at this point

        connections.add(gameID, ctx);

        // Send Load Game message to the ROOT client
        GameData gameData = gameDAO.getGameByID(gameID);

        var loadGameMsg = new LoadGameMessage(gameData.game());
         ctx.send(new Gson().toJson(loadGameMsg));

         // Send Notification to everyone
         String message = String.format("%s joined the game", username);
         String notificationMsg = new NotificationMessage(message).toString();
         connections.broadcast(command.getGameID(), notificationMsg);
    }



}