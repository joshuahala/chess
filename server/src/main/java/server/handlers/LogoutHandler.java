package server.handlers;
import dataaccess.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import model.*;

public class LogoutHandler implements Route  {

    UserService userService;
    public LogoutHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        // create the logoutRequest
        String authToken = req.headers("authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        // do stuff
        LogoutResult logoutResult = userService.logout(logoutRequest);
        // return logoutResult
        return new Gson().toJson(logoutResult);

    }
}