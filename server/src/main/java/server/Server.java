package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import spark.*;
import server.handlers.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.exception(DataAccessException.class, this::errorHandler);
        Spark.post("/user", new RegisterHandler());
        Spark.delete("/db", new ClearHandler());
        Spark.post("/session", new LoginHandler());

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
