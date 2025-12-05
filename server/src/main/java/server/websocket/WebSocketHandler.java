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
import websocket.commands.MakeMoveCommand;
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

    public void onMessage(WsMessageContext ctx) throws IOException, DataAccessException {
        String message = ctx.message();
        System.out.println("Received: " + message);

        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            // Connect
            case CONNECT -> {
                 handleConnect(ctx, command);
            }

            // Make Move
            case MAKE_MOVE -> {
                var moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                handleMakeMove(ctx, moveCommand);
            }

            // Leave

            // Resign

        }
    }

    public void onError(WsErrorContext ctx) {
        System.out.println("WebSocket error: " + ctx.error());
    }


    private void handleConnect(WsContext ctx, UserGameCommand command) throws IOException, DataAccessException {

//        System.out.println("DEBUG: Executing handleConnect for game " + command.getGameID());

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
        String notificationMsg = new Gson().toJson(new NotificationMessage(message));
        connections.broadcast(command.getGameID(), notificationMsg);
    }

    private void handleMakeMove(WsContext ctx, MakeMoveCommand command) throws IOException, DataAccessException {
        String authToken = command.getAuthToken();
        String username = authDAO.getAuth(authToken).username();
        var gameData = gameDAO.getGameByID(command.getGameID());
        var game = gameData.game();

        // 2. Validate: Is the game already over?
        // (You might have a 'isGameOver' boolean in your GameData or check checkmate)
        // if (gameData.isGameOver()) { ... error ... }

        // Is the player in the game
        ChessGame.TeamColor playerColor = null;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            // User is an observer, they cannot move
            sendError(ctx, "Error: Observers cannot make moves");
            return;
        }

        // Attempt the move
        try {

            // Make move
            game.makeMove(command.getMove());

            // On Success, update database
            gameDAO.updateGame(gameData.gameName(), gameData);

            // Broadcast to each player
            var loadGameMsg = new LoadGameMessage(game);
            connections.broadcast(command.getGameID(), new Gson().toJson(loadGameMsg));

            // Broadcast to observers
            String message = String.format("%s moved %s", username, command.getMove().toString());
            var notificationMsg = new NotificationMessage(message);

            connections.broadcast(command.getGameID(), new Gson().toJson(notificationMsg));

        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void sendError(WsContext ctx, String msg) {
        var errorMsg = new ErrorMessage(msg);
        ctx.send(new Gson().toJson(errorMsg));
    }



}