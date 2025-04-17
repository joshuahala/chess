package server.websocket;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    AuthDAO authDAO;
    UserDAO userDAO;
    GameDAO gameDAO;
    GameService gameService;
    ConnectionManager connections;
    HashMap<Integer, ConnectionManager> connectionManagers = new HashMap<>();
    public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        gameService = new GameService(userDAO, authDAO, gameDAO);
        connections = new ConnectionManager();
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
                    case LEAVE -> {leave(command, session);}
                    case CONNECT -> connect(command, session);
                    case RESIGN -> resign(command, session);
                    default -> {defaultCase();}
                }

            }


//            session.getRemote().sendString("WebSocket response: " + message);
        }
    }

    private void leave(UserGameCommand command, Session session) {
        try {
            setConnectionManager(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            // update game
            gameService.leave(command.getAuthToken(), command.getGameID());
            // notify self
//            NotificationMessage selfMessage = new NotificationMessage("You have left the game");
//            session.getRemote().sendString(new Gson().toJson(selfMessage));
            // broadcast
            GameData gameData = gameDAO.getGame(command.getGameID());
            String leftMessage = "";
            if (Objects.equals(command.getParticipantType(), "player")) {
                leftMessage = authData.username() + " has left the game.";
            } else if (Objects.equals(command.getParticipantType(), "observer")) {
                leftMessage = authData.username() + " is no longer observing.";
            }
            NotificationMessage broadcastMessage = new NotificationMessage(leftMessage);
            connections.broadcast(authData.authToken(), broadcastMessage);
            connections.remove(command.getAuthToken());
            connectionManagers.put(command.getGameID(), connections);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            setConnectionManager(command.getGameID());
            connections.add(command.getAuthToken(), session);
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                var message = authData.username() + " has joined the game as White";
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.broadcast(authData.authToken(),notificationMessage);
            } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
                var message = authData.username() + " has joined the game as Black";
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.broadcast(authData.authToken(),notificationMessage);
            } else {
                var obsMessage = authData.username() + " has joined the game as an Observer";
                NotificationMessage notificationMessage = new NotificationMessage(obsMessage);
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
            connectionManagers.put(command.getGameID(), connections);
        } catch (Exception ex) {
            ErrorMessage errorMessage = new ErrorMessage("An error occurred while trying to connect");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }
    private String convertChessPosition(ChessPosition position) {
        // Convert column number to letter (1 -> a, 2 -> b, etc.)
        char file = (char) ('a' + (position.getColumn() - 1));
        // Use row number as is since 1 is already bottom row
        return String.format("%c%d", file, position.getRow());
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            setConnectionManager(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                ErrorMessage errorMessage = new ErrorMessage("bad auth");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            }
            GameData currentGame = gameDAO.getGame(command.getGameID());
            if (Objects.equals(currentGame.gameOver(), "true")) {
                ErrorMessage errorMessage = new ErrorMessage("This game has ended");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            } else {
                // update db
                GameData gameData = gameService.makeMove(command);
                ChessGame game = gameData.game();
                // send back load message
                LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
                var json = new Gson().toJson(loadGameMessage);
                session.getRemote().sendString(json);
                // broadcast to update other connections
                LoadGameMessage loadMessage = new LoadGameMessage(gameData);
                connections.broadcast(command.getAuthToken(),loadMessage);

                // Convert positions to algebraic notation
                String startPos = convertChessPosition(command.move.getStartPosition());
                String endPos = convertChessPosition(command.move.getEndPosition());
                String moveString = authData.username() + " has made the move: " + startPos + " -> " + endPos;
                NotificationMessage moveNotification = new NotificationMessage(moveString);
                connections.broadcast(command.getAuthToken(), moveNotification);

                // check for check or checkmate

                if (game.isInCheckmate(game.getTeamTurn())) {
                    NotificationMessage checkNotification = new NotificationMessage(game.getTeamTurn() + " is in checkmate. That's the game.");
//                connections.broadcast(command.getAuthToken(), checkNotification);
                    gameService.endGame(gameData.gameID());
                } else if (game.isInCheck(game.getTeamTurn())) {
                    NotificationMessage checkNotification = new NotificationMessage(game.getTeamTurn() + " is in check.");
//                connections.broadcast(command.getAuthToken(), checkNotification);
                }

                if (game.isInStalemate(game.getTeamTurn())) {
                    NotificationMessage checkNotification = new NotificationMessage("It's a stalemate!");
//                connections.broadcast("", checkNotification);
                    gameService.endGame(gameData.gameID());
                }
            }

            connectionManagers.put(command.getGameID(), connections);

        } catch (Exception ex) {
            ErrorMessage errorMessage = new ErrorMessage("Cannot make that move");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        setConnectionManager(command.getGameID());
        if (command.getParticipantType() == "observer") {
            throw new Exception("Observers can leave, but not resign");
        }
        try {
            gameService.resign(command.getGameID(), command.getAuthToken());
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            var message = authData.username() + " has resigned.";
            NotificationMessage broadcastMessage = new NotificationMessage(message);
            connections.broadcast(authData.authToken(),broadcastMessage);
            NotificationMessage notificationMessage = new NotificationMessage("You have resigned");
            session.getRemote().sendString(new Gson().toJson(notificationMessage));
            connectionManagers.put(command.getGameID(), connections);
            // send back
        } catch (Exception ex) {
            System.out.printf("server error resignin: " + ex.getMessage());
            ErrorMessage errorMessage = new ErrorMessage("cannot resign");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void setConnectionManager(int gameID) {
        if (connectionManagers.get(gameID) == null) {
            connections = new ConnectionManager();
        } else {
            connections = connectionManagers.get(gameID);
        }
    }

    private void defaultCase() {

    }
}