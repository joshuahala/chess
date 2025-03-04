package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class CreateGameHandler implements Route {
    GameService gameService;
    public CreateGameHandler (MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
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
