package service;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;

public class UserServiceTests {
    @Test
    @DisplayName("Register User")
    public void registerNewUser() throws DataAccessException {
        UserService service = new UserService();
        String json = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        var userData = new Gson().fromJson(json, UserData.class);
        RegisterResult registerResult = service.register(userData);

        var result = new Gson().toJson(registerResult);

        Assertions.assertEquals(json, result,
                "These are not the same, bro");
    }
}
