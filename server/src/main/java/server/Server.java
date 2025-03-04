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
        Spark.post("/user", new RegisterHandler(userDAO, authDAO));
        Spark.delete("/db", new ClearHandler(userDAO, authDAO, gameDAO));
        Spark.post("/session", new LoginHandler(userDAO, authDAO));
        Spark.delete("/session", new LogoutHandler(userDAO, authDAO));
        Spark.post("/game", new CreateGameHandler(userDAO, authDAO, gameDAO));
        Spark.get("/game", new ListGamesHandler(userDAO, authDAO, gameDAO));
        Spark.put("/game", new JoinGameHandler(userDAO, authDAO, gameDAO));

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
