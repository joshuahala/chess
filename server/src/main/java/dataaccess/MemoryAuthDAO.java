package dataaccess;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    HashMap<String,AuthData> authList = new HashMap<>();

    public void createAuth(String authToken, AuthData authData) {
        authList.put(authToken, authData);
    }
    public AuthData getAuth(String authToken) {
        return this.authList.get(authToken);
    }
    public void deleteAllAuthData() {
        authList = new HashMap<>();
    }
    public void deleteAuth(String authToken) {
        this.authList.remove(authToken);
    }
}
