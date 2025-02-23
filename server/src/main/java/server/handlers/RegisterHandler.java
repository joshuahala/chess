package server.handlers;
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

        return "Hello there!";
    }
}