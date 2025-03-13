package dataaccess;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.*;
import dataaccess.*;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class SqlUserDAOTests {

    private static SqlUserDAO userDAO;
    private static SqlAuthDAO authDAO = null;
    private static SqlGameDAO gameDAO = null;

    @BeforeAll
    public static void init() throws DataAccessException {


        try {
            userDAO = new SqlUserDAO();
            authDAO = new SqlAuthDAO();
            gameDAO = new SqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public  void beforeEach() throws DataAccessException {
        clearDB();
    }
    @AfterEach
    public void afterEach() throws DataAccessException {
        clearDB();
    }

    // create user positive test
    @Test
    @DisplayName("Register New User Test")
    public void registerUserTest() throws DataAccessException {
        // create connection to db
        // register user
        UserData newUser = new UserData("jon", "pass", "jb@gmail");
        userDAO.createUser(newUser);

        // check if user is in db
        Assertions.assertTrue(checkForUser(newUser.username()));
    }

    // create user negative test
    @Test
    @DisplayName("Register user twice throws error")
    public void registerUserTwice() throws DataAccessException {
        // create user with empty email
        UserData newUser = new UserData("jon", "pass", "jb@gmail");
        userDAO.createUser(newUser);
        // assert error is thrown
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(newUser);
        });
    }

    // get user positive test
    @Test
    @DisplayName("get User Test")
    public void getUserTest() throws DataAccessException {
        // create user
        UserData newUser = new UserData("jon", "pass", "jb@gmail");
        userDAO.createUser(newUser);
        // userDAO.getUser()
        UserData fetchedUser = userDAO.getUser("jon");

        // assert is the same as the expected
        Assertions.assertEquals(newUser, fetchedUser);

    }


    // get user negative test
    @Test
    @DisplayName("Get non-existent user throws error")
    public void getNonExistentUser() throws DataAccessException {
        // fetching a non-existent user throws an error

        Assertions.assertThrows(DataAccessException.class, ()-> {
            UserData fetchedUser = userDAO.getUser("nobody");
        });

    }

    // deleteAll test
    @Test
    @DisplayName("delete All Test")
    void deleteAllTest() throws DataAccessException {
        // create user and populate database
        UserData newUser = new UserData("jon", "pass", "jb@gmail");
        userDAO.createUser(newUser);

        // clear database
        userDAO.deleteAllUsers();

        // assert database is empty
        Assertions.assertThrows(DataAccessException.class, ()-> {
            userDAO.getUser("jon");
        });
    }


    private boolean checkForUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email, json FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("username") != null) {
                            return Objects.equals(rs.getString("username"), username);
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return false;

    }

    private static void clearDB() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String[] tables = {"users", "auth", "games"};

            for (String table : tables) {
                var statement = "TRUNCATE TABLE " + table;
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to clear data: %s", e.getMessage()));
        }
    }
}


