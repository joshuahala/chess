package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SqlAuthDAOTests {

    private static SqlAuthDAO authDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        clearDB();
        authDAO = new SqlAuthDAO();
    }

    @Test
    @DisplayName("Create Auth Positive Test")
    public void createAuthPositiveTest() throws DataAccessException {
        AuthData authData = new AuthData("token123", "user1");
        authDAO.createAuth("token123", authData);
        AuthData fetchedAuth = authDAO.getAuth("token123");
        assertEquals(authData, fetchedAuth);
    }

    @Test
    @DisplayName("Create Auth With Bad Input")
    public void createAuthBadInputTest() throws DataAccessException {
        AuthData authData = new AuthData("token123", "user1");
        authDAO.createAuth("token123", authData);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth("", authData));
    }

    @Test
    @DisplayName("Get Auth Positive Test")
    public void getAuthPositiveTest() throws DataAccessException {
        AuthData authData = new AuthData("token456", "user2");
        authDAO.createAuth("token456", authData);
        AuthData fetchedAuth = authDAO.getAuth("token456");
        assertEquals(authData, fetchedAuth);
    }

    @Test
    @DisplayName("Get Auth Negative Test")
    public void getAuthNegativeTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("nonexistentToken"));
    }

    @Test
    @DisplayName("Delete Auth Positive Test")
    public void deleteAuthPositiveTest() throws DataAccessException {
        AuthData authData = new AuthData("token789", "user3");
        authDAO.createAuth("token789", authData);
        authDAO.deleteAuth("token789");
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("token789"));
    }
    

    @Test
    @DisplayName("Delete All Auth Data Test")
    public void deleteAllAuthDataTest() throws DataAccessException {
        AuthData authData1 = new AuthData("token101", "user4");
        AuthData authData2 = new AuthData("token102", "user5");
        authDAO.createAuth("token101", authData1);
        authDAO.createAuth("token102", authData2);
        authDAO.deleteAllAuthData();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("token101"));
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("token102"));
    }

    private static void clearDB() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String[] tables = {"auth"};
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