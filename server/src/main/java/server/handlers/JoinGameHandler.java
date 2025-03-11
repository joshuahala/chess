package server.handlers;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Collection;

public class JoinGameHandler implements Route {
    GameService gameService;
    public JoinGameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        gameService.joinGame(joinGameRequest, authToken);
        return new Gson().toJson(new JoinGameResult());
    }
}
