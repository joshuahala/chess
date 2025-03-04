package service;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import spark.utils.Assert;

import java.util.Locale;

public class UserServiceTests {
    private UserService service;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Register User")
    public void registerNewUser() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        String expectedName = "Jeremy";

        var userData = new Gson().fromJson(jsonInput, UserData.class);
        RegisterResult registerResult = service.register(userData);
        var resultName = registerResult.username();
        var resultToken = registerResult.authToken();

        Assertions.assertEquals(expectedName, resultName,
                "These are not the same, bro");
        Assertions.assertInstanceOf(String.class,resultToken);
    }

    @Test
    @DisplayName("Try to register user twice")
    public void registerUserTwiceGivesError() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        String expectedName = "Jeremy";
        var userData = new Gson().fromJson(jsonInput, UserData.class);
        service.register(userData);
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.register(userData);
        });
    }

    @ParameterizedTest
    @DisplayName("Empty Strings in register request")
    @CsvSource({
            "'',secret,jeremy@gmail.com", // Empty username
            "username,'',jeremy@gmail.com", // Empty password
            "username,secret,''"          // Empty email
    })
    public void emptyRegisterStringThrowsError(String username, String password, String email) {
        // Arrange: Set up the UserService and the input JSON
        String jsonInput = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}", username, password, email);

        var userData = new Gson().fromJson(jsonInput, UserData.class);

        // Act & Assert: Check that the exception is thrown when registering with empty fields
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.register(userData);
        });
    }

    @Test
    @DisplayName("Get User Info")
    public void getUserInfo() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";

        var userData = new Gson().fromJson(jsonInput, UserData.class);
        UserData expectedUser = new Gson().fromJson(jsonInput, UserData.class);
        RegisterResult registerResult = service.register(userData);
        UserData retrievedUser = service.userDAO.getUser("Jeremy");

        Assertions.assertEquals(expectedUser, retrievedUser,
                "This is not the user you are looking for");
    }

    @Test
    public void getUserOnEmptyDataBaseReturnsError() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";

        var userData = new Gson().fromJson(jsonInput, UserData.class);
        RegisterResult registerResult = service.register(userData);
        service.deleteAllUsers();
        UserData retrievedUser = service.userDAO.getUser("Jeremy");

        Assert.isTrue(retrievedUser == null, "Actually it does exist");
    }

    @Test
    public void loginUser() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        var userData = new Gson().fromJson(jsonInput, UserData.class);
        String expectedName = "Jeremy";

        // register user
        RegisterResult registerResult = service.register(userData);
        // login
        LoginRequest loginRequest = new LoginRequest("Jeremy", "secret");
        LoginResult loginResult = service.login(loginRequest);

        // assert that login was successful and the response contains the user's name and an authtoken
        String resultName = loginResult.username();
        String resultToken = loginResult.authToken();
        Assertions.assertEquals(expectedName, resultName,
                "These names are not the same, bro");
        Assertions.assertInstanceOf(String.class,resultToken);
    }

    @Test
    public void logoutUser() throws DataAccessException {
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        var userData = new Gson().fromJson(jsonInput, UserData.class);
        String expectedName = "Jeremy";

        // register user
        RegisterResult registerResult = service.register(userData);
        // login
        LoginRequest loginRequest = new LoginRequest("Jeremy", "secret");
        LoginResult loginResult = service.login(loginRequest);
        // logout
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        LogoutResult logoutResult = service.logout(logoutRequest);
        // Assert that the authData has been deleted
        AuthData authData = service.authDAO.getAuth(loginResult.authToken());
        System.out.println(authData);
        Assertions.assertTrue(authData == null, "Error: authdata was expected to be deleted, but it was found");
    }
}
