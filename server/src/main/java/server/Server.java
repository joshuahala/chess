package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.UserService;
import spark.*;
import server.handlers.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //UserService userService = new UserService();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();


        // Register your endpoints and handle exceptions here.
        Spark.exception(DataAccessException.class, this::errorHandler);
        Spark.post("/user", new RegisterHandler(userDAO));
        Spark.delete("/db", new ClearHandler());
        Spark.post("/session", new LoginHandler(userDAO));
        Spark.delete("/session", new LogoutHandler(userDAO));
        Spark.post("/game", new CreateGameHandler(gameDAO, authDAO));

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
