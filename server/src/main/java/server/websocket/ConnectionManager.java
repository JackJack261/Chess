package server.websocket;

import io.javalin.websocket.WsContext;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<WsContext>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, WsContext ctx) {
        connections.putIfAbsent(gameID, new ArrayList<>());
        connections.get(gameID).add(ctx);
    }

    public void remove(WsContext ctx) {
        for (var list : connections.values()) {
            list.remove(ctx);
        }
    }

    public void broadcast(Integer gameID, String message) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            for (var ctx : gameConnections) {
                if (ctx.session.isOpen()) {
                    ctx.send(message);
                }
            }
        }
    }
}


