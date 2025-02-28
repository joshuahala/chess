package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.LoginRequest;
import model.LoginResult;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        UserService userService = new UserService();
        LoginResult loginResult = userService.login(loginRequest);
        return loginResult;
    }
}
