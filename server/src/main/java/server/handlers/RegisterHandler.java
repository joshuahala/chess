package server.handlers;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        return "Hello there!";
    }
}