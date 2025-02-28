package service;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;
import spark.utils.Assert;

public class UserServiceTests {
    @Test
    @DisplayName("Register User")
    public void registerNewUser() throws DataAccessException {
        UserService service = new UserService();
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
    @DisplayName("Get User Info")
    public void getUserInfo() throws DataAccessException {
        UserService service = new UserService();
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
        UserService service = new UserService();
        String jsonInput = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";

        var userData = new Gson().fromJson(jsonInput, UserData.class);
        RegisterResult registerResult = service.register(userData);
        service.deleteAllUsers();
        UserData retrievedUser = service.userDAO.getUser("Jeremy");

        Assert.isTrue(retrievedUser == null, "Actually it does exist");
    }
}
