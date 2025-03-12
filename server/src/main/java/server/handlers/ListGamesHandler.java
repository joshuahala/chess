package server.handlers;

import com.google.gson.Gson;
import dataaccess.*;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import model.ListGamesResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Collection;

public class ListGamesHandler implements Route {
    GameService gameService;
    public ListGamesHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        ListGamesResult listGamesResult = gameService.getAll(req.headers("authorization"));
        return new Gson().toJson(listGamesResult);
    }
}
