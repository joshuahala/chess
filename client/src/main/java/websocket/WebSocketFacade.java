package websocket;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.WsObserver;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {


    public Session session;
    private WsObserver wsObserver;

    public WebSocketFacade(WsObserver observer) throws Exception {

        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        wsObserver = observer;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                parseMessage(message);

            }
        });
    }

    private void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void leave(String authToken, Integer gameID){
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void connect(String authToken, int gameID){
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void parseMessage(String message) {
        if (message.contains("NOTIFICATION")) {
            NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
            System.out.println(notification.getMessage());
        }
        if (message.contains("LOAD")) {
            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            wsObserver.handleMessage(loadGameMessage);
        }
    }
}