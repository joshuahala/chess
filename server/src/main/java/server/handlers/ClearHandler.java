package server.handlers;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class ClearHandler implements Route {
    MemoryUserDAO userDAO;
    MemoryAuthDAO authDAO;
    public ClearHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        UserService userService = new UserService(userDAO, authDAO);
        userService.deleteAllUsers();
        userService.deleteAllAuthData();
        return new Gson().toJson(res);
    }
}