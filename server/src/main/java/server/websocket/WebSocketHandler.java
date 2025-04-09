package server.websocket;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    AuthDAO authDAO;
    UserDAO userDAO;
    GameDAO gameDAO;
    public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        Spark.port(8080);
        Spark.webSocket("/ws", WebSocketHandler.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
//        System.out.printf("Received: %s", message);
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch(command.getCommandType()) {
            case LEAVE -> {leave();}
            default -> {defaultCase();}
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    private void leave(String authToken, int gameID) {
        gameDAO.getGame()
    }

    private void defaultCase() {

    }
}