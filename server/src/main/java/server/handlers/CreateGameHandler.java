package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class CreateGameHandler implements Route {
    GameService gameService;
    public CreateGameHandler (MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        CreateGameReqBody body = new Gson().fromJson(req.body(), CreateGameReqBody.class);

        String authToken = req.headers("authorization");
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, body.gameName());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        return new Gson().toJson(createGameResult);
    }
}
