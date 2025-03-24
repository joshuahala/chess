package client;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import sharedserver.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:8080");
    }

    @BeforeEach
    public void beforeEach() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(userData);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void registerFail() throws Exception {
        // no email.
        UserData userData = new UserData("player1", "password", "");
        Assertions.assertThrows(ResponseException.class, ()-> {
            var authData = facade.register(userData);

        });
    }

}
