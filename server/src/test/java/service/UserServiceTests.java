package service;
import com.google.gson.Gson;
import model.UserData;
import org.junit.jupiter.api.*;

public class UserServiceTests {
    @Test
    @DisplayName("Register User")
    public void registerNewUser() {
        UserService service = new UserService();
        String json = "{\"username\":\"Jeremy\",\"password\":\"secret\",\"email\":\"jeremy@gmail.com\"}";
        var userData = new Gson().fromJson(json, UserData.class);
        userData = service.register(userData);

        var result = new Gson().toJson(userData);

        Assertions.assertEquals(json, result,
                "These are not the same, bro");
    }
}
