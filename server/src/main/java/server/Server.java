package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import service.UserService;
import spark.*;
import server.handlers.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserService userService = new UserService();


        // Register your endpoints and handle exceptions here.
        Spark.exception(DataAccessException.class, this::errorHandler);
        Spark.post("/user", new RegisterHandler(userService));
        Spark.delete("/db", new ClearHandler());
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public void errorHandler(DataAccessException e, Request req, Response res) {
        res.status(e.getErrorCode());
        res.body(e.getBody());

    }
}
