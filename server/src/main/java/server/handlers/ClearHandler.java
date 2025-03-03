package server.handlers;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        UserService userService = new UserService();
        userService.deleteAllUsers();
        userService.deleteAllAuthData();
        return new Gson().toJson(res);
    }
}