package client;

import client.websocket.NotificationHandler;
import jakarta.websocket.*;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{

    public Session session;
    public NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.notify(loadGame);
                        }
                        case ERROR -> {
                            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.notify(error);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notifyMessage = new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notifyMessage);
                        }
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception("500: " + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception("500: " + ex.getMessage());
        }
    }

}
