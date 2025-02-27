package dataaccess;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    HashMap<String,AuthData> authList = new HashMap<>();

    public void createAuth(String authToken, AuthData authData) {
        authList.put(authToken, authData);
    }
}
