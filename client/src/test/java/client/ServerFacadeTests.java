package client;

import exception.ResponseException;
import model.LoginRequest;
import model.LogoutRequest;
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
        facade = new ServerFacade(port);
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
    void registerTest() throws Exception {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(userData);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void registerFailTest() throws Exception {
        // no email.
        UserData userData = new UserData("player1", "password", "");
        Assertions.assertThrows(ResponseException.class, ()-> {
            facade.register(userData);

        });
    }
    @Test
    void loginTest() throws Exception{
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(userData);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        var loginResult = facade.login(loginRequest);
        Assertions.assertEquals(authData.username(), loginResult.username());
    }
    @Test
    void loginFailTest() throws Exception{
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(userData);
        // wrong password
        LoginRequest loginRequest = new LoginRequest("player1", "wrongpassword");
        Assertions.assertThrows(ResponseException.class, ()->{
            facade.login(loginRequest);
        });
    }
    @Test
    void logoutTest() throws Exception{
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var registerResult = facade.register(userData);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        var loginResult = facade.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        var logoutResult = facade.logout(logoutRequest);
        Assertions.assertTrue(logoutResult.isSuccess());
    }

//    @Test
//    void clearTest() throws Exception {
//        // no email.
//        UserData userData = new UserData("player1", "password", "email");
//        facade.register(userData);
//        facade.clear();
//
//    }


}
