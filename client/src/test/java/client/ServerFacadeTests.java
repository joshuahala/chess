package client;

import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import sharedserver.ServerFacade;

import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
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
    @Test
    void logoutFailTest() throws Exception{
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var registerResult = facade.register(userData);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        var loginResult = facade.login(loginRequest);
        // bad token
        var badToken = "not a token";
        LogoutRequest logoutRequest = new LogoutRequest(badToken);
        Assertions.assertThrows(ResponseException.class, ()->{
            var logoutResult = facade.logout(logoutRequest);
        });
    }

    @Test
    public void createGameTest() throws Exception {
        LoginResult loginResult = loginUser();
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "GameName");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        var gameId = createGameResult.gameID();
        Assertions.assertTrue(gameId > 0);
    }
    @Test
    public void createGameFailTest() throws Exception {
        LoginResult loginResult = loginUser();
        // no game name given
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "");
        Assertions.assertThrows(ResponseException.class, ()->{
            CreateGameResult createGameResult = facade.createGame(createGameRequest);
        });
    }

    @Test
    public void listGamesTest() throws Exception {
        LoginResult loginResult = loginUser();
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "myGame");
        Assertions.assertNotNull(facade.listGames(loginResult.authToken()));
    }
    @Test
    public void listGamesFailTest() throws Exception {
        LoginResult loginResult = loginUser();
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "myGame");
        var badToken = "bad";
        Assertions.assertThrows(ResponseException.class, ()->{
            facade.listGames(badToken);
        });
    }

    @Test
    public void joinGameTest() throws Exception {
        LoginResult loginResult = loginUser();
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "myGame");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        var gameID = createGameResult.gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("black", Integer.toString(gameID));
        var result = facade.joinGame(joinGameRequest, loginResult.authToken());
        Assertions.assertNotNull(result);
    }

    @Test
    public void joinGameFailTest() throws Exception {
        LoginResult loginResult = loginUser();
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "myGame");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        var gameID = createGameResult.gameID();
        // invalid color: purple
        JoinGameRequest joinGameRequest = new JoinGameRequest("purple", Integer.toString(gameID));
        Assertions.assertThrows(ResponseException.class, ()->{
            facade.joinGame(joinGameRequest, loginResult.authToken());
        });
    }

    @Test
    void clearTest() throws Exception {
        // create game, user, and auth data
        // player1, password, p1@email.com
        // myGame
        LoginResult loginResult = loginUser();
        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "myGame");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        // clear db tables
        facade.clear();

        // Assert user data doesn't exist
        Assertions.assertThrows(ResponseException.class, ()->{
            facade.login(new LoginRequest("username", "password"));
        });

    }

    private LoginResult loginUser() throws Exception {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var registerResult = facade.register(userData);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        var loginResult = facade.login(loginRequest);
        return loginResult;
    }
}
