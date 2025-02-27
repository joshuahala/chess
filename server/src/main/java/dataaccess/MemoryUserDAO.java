package dataaccess;

import org.eclipse.jetty.server.Authentication;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> userList = new HashMap<>();

    public UserData createUser(UserData userData) {
        userList.put(userData.username(), userData);
        return userData;
    }


    public UserData getUser(String username) {
        return userList.get(username);
    }

    public void deleteUser(String username) {
        userList.remove(username);
    }
}
