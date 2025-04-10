package server.websocket;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.GameService;
import spark.Spark;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommandDeserializer;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    AuthDAO authDAO;
    UserDAO userDAO;
    GameDAO gameDAO;
    GameService gameService;
    ConnectionManager connections = new ConnectionManager();
    public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        if (message != null) {
            if (message.contains("MOVE")) {
                MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(command, session);
            } else {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                switch(command.getCommandType()) {
                    case LEAVE -> {leave(command);}
                    case CONNECT -> connect(command, session);
                    default -> {defaultCase();}
                }
            }

//            session.getRemote().sendString("WebSocket response: " + message);
        }
    }

    private void leave(UserGameCommand command) {
        try {
            gameService.leave(command.getAuthToken(), command.getGameID());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void connect(UserGameCommand command, Session session) {
        try {
            connections.add(command.getAuthToken(), session);
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                var message = authData.username() + " has joined the game as White";
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.broadcast(authData.authToken(),notificationMessage);
            }
            if (Objects.equals(authData.username(), gameData.blackUsername())) {
                var message = authData.username() + " has joined the game as Black";
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.broadcast(authData.authToken(),notificationMessage);
            } else {
                var message = authData.username() + " has joined the game as an Observer";
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.broadcast(authData.authToken(),notificationMessage);
            }
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            loadGameMessage.username = authData.username();
            if (Objects.equals(gameData.whiteUsername(), authData.username())) {
                loadGameMessage.team = "white";
            }
            if (Objects.equals(gameData.blackUsername(), authData.username())) {
                loadGameMessage.team="black";
            }
            var json = new Gson().toJson(loadGameMessage);

            session.getRemote().sendString(json);
        } catch (Exception ex) {
            System.out.println("Error connecting");
        }
    }
    private void makeMove(MakeMoveCommand command, Session session) {
        try {
            System.out.println("received move command");
            GameData gameData = gameService.makeMove(command);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            var json = new Gson().toJson(loadGameMessage);
            session.getRemote().sendString(json);
        } catch (Exception ex) {
            System.out.println("Server experienced error with move command: " + ex.getMessage());
        }

    }

    private void defaultCase() {

    }
}