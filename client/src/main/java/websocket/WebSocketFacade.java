package websocket;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.EscapeSequences;
import ui.WsObserver;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
//                System.out.println(message);

            }
        });
    }

    private void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void leave(String authToken, Integer gameID, String type){
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            command.setParticipantType(type);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void resign(String authToken, int gameID, String participantType) {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            command.setParticipantType(participantType);
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
    public void makeMove(MakeMoveCommand moveCommand) {
        try {
            var jsonCommand = new Gson().toJson(moveCommand);
            System.out.println("sending from ws facade to server");
            this.session.getBasicRemote().sendText(jsonCommand);
        } catch (Exception ex) {
            System.out.println("Error making move");
        }
    }



    public void parseMessage(String message) {
        if (message.contains("NOTIFICATION")) {
            NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
            if (!notification.getMessage().contains("You have")) {
                System.out.printf("%n" + notification.getMessage() + "%n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>>" + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
        if (message.contains("LOAD")) {
            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            wsObserver.handleMessage(loadGameMessage);
        }
        if (message.contains("ERROR")) {
            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
            System.out.println(errorMessage.getMessage());
        }
    }
}