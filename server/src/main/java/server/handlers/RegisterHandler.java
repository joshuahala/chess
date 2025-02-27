package server.handlers;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import model.*;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        UserService service = new UserService();
        RegisterResult registerResult = service.register(userData);

        return new Gson().toJson(registerResult);
    }
}