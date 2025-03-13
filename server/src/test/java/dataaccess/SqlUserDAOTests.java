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
    public static void init() {

        try {
            userDAO = new SqlUserDAO();
            authDAO = new SqlAuthDAO();
            gameDAO = new SqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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
    // create user with empty email
    // assert error is thrown

    // get user positive test
    // userDAO.getUser()
    // assert is the same as the expected

    // get user negative test
    // fetching a non-existent user throws an error

    // deleteAll test
    // call deleteAll()
    // assert db is empty

    private boolean checkForUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email, json FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (Objects.equals(rs.getString("username"), username)){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }

    }
}


