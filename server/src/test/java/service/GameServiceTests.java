package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameServiceTests {
    @Test
    @DisplayName("TestCreateGame")
    public void testCreateGame() throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        var userData = new Gson().fromJson(jsonInput, UserData.class);

        // register user
        RegisterResult registerResult = userService.register(userData);
        // login
        LoginRequest loginRequest = new LoginRequest("Jeremy", "secret");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        // create Game
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, "myGame"));
        Assertions.assertEquals(1235, createGameResult.gameID());
    }

    @Test
    @DisplayName("ListGames")
    public void listGamesTest() throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);


        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        var userData = new Gson().fromJson(jsonInput, UserData.class);

        // register user
        RegisterResult registerResult = userService.register(userData);
        // login
        LoginRequest loginRequest = new LoginRequest("Jeremy", "secret");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        // create Game
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, "myGame"));
        Assertions.assertEquals(1235, createGameResult.gameID());

        Collection<GameDataWithoutGames> expectedGames = new ArrayList<>();
        expectedGames.add(new GameDataWithoutGames(1235, null, null, "myGame"));
        ListGamesResult expectedResult = new ListGamesResult(expectedGames);

        // list games
        ListGamesResult listGamesResult = gameService.getAll();
        Assertions.assertEquals(expectedResult,listGamesResult, "not the same list of games!");
    }
}
