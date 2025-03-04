package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
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
    public ListGamesHandler(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        ListGamesResult listGamesResult = gameService.getAll();
        return new Gson().toJson(listGamesResult);
    }
}
