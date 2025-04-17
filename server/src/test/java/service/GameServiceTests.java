package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameServiceTests {
    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();
    MemoryGameDAO gameDAO = new MemoryGameDAO();
    UserService userService = new UserService(userDAO, authDAO);
    GameService gameService = new GameService(userDAO, authDAO, gameDAO);

    UserData userData;
    String authToken;

    RegisterResult registerResult;

    @BeforeEach
    public void init() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        userData = new Gson().fromJson(jsonInput, UserData.class);

        // register user
        registerResult = userService.register(userData);
        // login
        LoginRequest loginRequest = new LoginRequest("Jeremy", "secret");
        LoginResult loginResult = userService.login(loginRequest);
        authToken = loginResult.authToken();
    }
    @Test
    @DisplayName("TestCreateGame")
    public void testCreateGame() throws DataAccessException {



        // create Game
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, "myGame"));
        Assertions.assertEquals(1235, createGameResult.gameID());
    }

    @Test
    @DisplayName("Create with bad values")
    public void createGameBadValues() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, null));
        });

    }

    @Test
    @DisplayName("List Games with bad auth")
    public void listGamesBadAuth() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest("badToken", "gameName"));
        });

    }

    @Test
    @DisplayName("ListGames")
    public void listGamesTest() throws DataAccessException {

        // create Game
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, "myGame"));
        Assertions.assertEquals(1235, createGameResult.gameID());

        Collection<GameDataWithoutGames> expectedGames = new ArrayList<>();
        expectedGames.add(new GameDataWithoutGames(1235, null, null, "myGame", "false"));
        ListGamesResult expectedResult = new ListGamesResult(expectedGames);
        // use authToken

        // list games
        ListGamesResult listGamesResult = gameService.getAll(authToken);
        Assertions.assertEquals(expectedResult,listGamesResult, "not the same list of games!");
    }

    @Test
    @DisplayName("Join Game with bad ID")
    public void joinGameBadID() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
        gameService.joinGame(new JoinGameRequest("white", "0000"), authToken);
        });
    }

    @Test
    @DisplayName("Join Game")
    public void joinGame() throws DataAccessException {
        // create Game
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, "myGame"));
        // join game
        gameService.joinGame(new JoinGameRequest("white", "1235"), authToken);
        // check if User joined game
        GameData gameData = gameService.gameDAO.getGame(1235);
        Assertions.assertEquals(userData.username(), gameData.whiteUsername());

    }

    @Test
    @DisplayName("Delete All Games")
    public void deleteAllGames() throws DataAccessException {
        // create Game
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, "myGame"));
        // check if game is there
        Assertions.assertNotNull(gameService.getAll(authToken));
        // delete games
        gameService.deleteAllGames();
        // check for games
        Collection<GameDataWithoutGames> games = new ArrayList<>();
        ListGamesResult listGamesResult = gameService.getAll(authToken);
        ArrayList<GameDataWithoutGames> expected = new ArrayList<>();
        Assertions.assertEquals(expected, listGamesResult.games());
    }
}
