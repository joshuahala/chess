package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SqlGameDAOTests {

    private static SqlGameDAO gameDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        clearDB();
        gameDAO = new SqlGameDAO();
    }
    @BeforeEach
    public  void beforeEach() throws DataAccessException {
        clearDB();
    }

    @Test
    @DisplayName("Create Game with Valid Data")
    public void testCreateGameWithValidData() throws DataAccessException {
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Game1", new ChessGame());
        gameDAO.createGame(1, gameData);
        GameData fetchedGame = gameDAO.getGame(1);
        assertEquals(gameData.gameID(), fetchedGame.gameID());
        assertEquals(gameData.whiteUsername(), fetchedGame.whiteUsername());
        assertEquals(gameData.gameName(), fetchedGame.gameName());
    }

    @Test
    @DisplayName("Create Game with Duplicate ID Throws Exception")
    public void testCreateGameWithDuplicateIDThrowsException() throws DataAccessException {
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Game1", new ChessGame());
        gameDAO.createGame(1, gameData);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(1, gameData));
    }

    @Test
    @DisplayName("Retrieve Game with Valid ID")
    public void testRetrieveGameWithValidID() throws DataAccessException {
        GameData gameData = new GameData(2, "whitePlayer2", "blackPlayer2", "Game2", new ChessGame());
        gameDAO.createGame(2, gameData);
        GameData fetchedGame = gameDAO.getGame(2);
        assertEquals(gameData.gameID(), fetchedGame.gameID());
        assertEquals(gameData.whiteUsername(), fetchedGame.whiteUsername());
        assertEquals(gameData.gameName(), fetchedGame.gameName());
    }

    @Test
    @DisplayName("Retrieve Game with Invalid ID Throws Exception")
    public void testRetrieveGameWithInvalidIDThrowsException() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(999));
    }

    @Test
    @DisplayName("Update Game with Valid Data")
    public void testUpdateGameWithValidData() throws DataAccessException {
        GameData gameData = new GameData(3, "whitePlayer3", "blackPlayer3", "Game3", new ChessGame());
        gameDAO.createGame(3, gameData);
        GameData updatedGameData = new GameData(3, "whitePlayer3Updated", "blackPlayer3Updated", "Game3", new ChessGame());
        gameDAO.updateGame(updatedGameData);
        GameData fetchedGame = gameDAO.getGame(3);
        assertEquals(updatedGameData.gameID(), fetchedGame.gameID());
        assertEquals(updatedGameData.whiteUsername(), fetchedGame.whiteUsername());
        assertEquals(updatedGameData.gameName(), fetchedGame.gameName());
    }

    @Test
    @DisplayName("Update Game with Invalid ID Throws Exception")
    public void testUpdateGameWithInvalidIDThrowsException() throws DataAccessException {
        GameData gameData = new GameData(999, "whitePlayer", "blackPlayer", "Game", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameData));
    }

    @Test
    @DisplayName("Delete All Games")
    public void testDeleteAllGames() throws DataAccessException {
        GameData gameData1 = new GameData(4, "whitePlayer4", "blackPlayer4", "Game4", new ChessGame());
        GameData gameData2 = new GameData(5, "whitePlayer5", "blackPlayer5", "Game5", new ChessGame());
        gameDAO.createGame(4, gameData1);
        gameDAO.createGame(5, gameData2);
        gameDAO.deleteAll();
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(4));
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(5));
    }

    private static void clearDB() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String[] tables = {"games"};
            for (String table : tables) {
                var statement = "TRUNCATE TABLE " + table;
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Unable to clear data: %s", e.getMessage()));
        }
    }
} 