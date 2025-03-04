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
}
