package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
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
    public ListGamesHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        ListGamesResult listGamesResult = gameService.getAll();
        return new Gson().toJson(listGamesResult);
    }
}
