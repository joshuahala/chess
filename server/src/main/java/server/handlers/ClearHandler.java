package server.handlers;
import dataaccess.*;
import model.JoinGameResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class ClearHandler implements Route {
//    MemoryUserDAO userDAO;
//    MemoryAuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;
    AuthDAO authDAO;
    public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);

        userService.deleteAllUsers();
        userService.deleteAllAuthData();
        gameService.deleteAllGames();
        return new Gson().toJson(new JoinGameResult());
    }
}