package server.handlers;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import model.*;

public class LogoutHandler implements Route  {

    UserService userService;
    public LogoutHandler(UserService userService) {
        this.userService = userService;
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