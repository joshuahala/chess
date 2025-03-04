package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.LoginRequest;
import model.LoginResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    GameService gameService;
    public CreateGameHandler (MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        CreateGameRequest createGameRequest = new CreateGameRequest(req.headers("authorization"), req.body());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        return new Gson().toJson(createGameResult);
    }
}
