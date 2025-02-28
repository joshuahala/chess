package server.handlers;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import model.*;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        UserService userService = new UserService();
        userService.deleteAllUsers();
        userService.deleteAllAuthData();
        return new Gson().toJson(res);
    }
}