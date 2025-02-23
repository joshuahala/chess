package server.handlers;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import model.UserData;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        var userData = new Gson().fromJson(req.body(), UserData.class);
        UserService service = new UserService();
        userData = service.register(userData);

        return new Gson().toJson(userData);
    }
}