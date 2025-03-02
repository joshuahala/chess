package dataaccess;
import model.AuthData;
public interface AuthDAO {
    void createAuth(String authToken, AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAllAuthData() throws DataAccessException;
    void deleteAuth(String authtoken) throws DataAccessException;
}
