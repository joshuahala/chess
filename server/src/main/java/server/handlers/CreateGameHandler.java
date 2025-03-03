package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.LoginRequest;
import model.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    GameService gameService;
    public CreateGameHandler (GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        return new Gson().toJson(createGameResult);
    }
}
