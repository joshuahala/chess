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

public class RegisterHandler implements Route  {

    UserService userService;
    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        RegisterResult registerResult = userService.register(userData);

        return new Gson().toJson(registerResult);
    }
}