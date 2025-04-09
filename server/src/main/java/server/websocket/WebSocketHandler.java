package server.websocket;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.GameService;
import spark.Spark;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    AuthDAO authDAO;
    UserDAO userDAO;
    GameDAO gameDAO;
    GameService gameService;
    public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        gameService = new GameService(userDAO, authDAO, gameDAO);

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        if (message != null) {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch(command.getCommandType()) {
                case LEAVE -> {leave(command);}
                default -> {defaultCase();}
            }
            session.getRemote().sendString("WebSocket response: " + message);
        }
    }

    private void leave(UserGameCommand command) {
        try {
            gameService.leave(command.getAuthToken(), command.getGameID());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void defaultCase() {

    }
}