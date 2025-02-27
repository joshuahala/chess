package dataaccess;
import model.AuthData;
public interface AuthDAO {
    void createAuth(String authToken, AuthData authData) throws DataAccessException;
}
