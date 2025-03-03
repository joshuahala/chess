package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.LoginRequest;
import model.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    UserService userService;
    public CreateGameHandler (UserService userService) {
        this.userService = userService;
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        return new Gson().toJson(loginResult);
    }
}
