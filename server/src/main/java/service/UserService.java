package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.eclipse.jetty.util.log.Log;


import java.util.Objects;
import java.util.UUID;

public class UserService {
    public MemoryUserDAO userDAO = new MemoryUserDAO();
    public MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public RegisterResult register(UserData userData) throws DataAccessException {
        //check if user exists
        if (userDAO.getUser(userData.username()) != null) {
            throw new DataAccessException(403,"This user already exists");
        }
        //if not, then create user data
        //add userdata
        userDAO.createUser(userData);
        //create authData
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());
        //add authData to database
        authDAO.createAuth(authToken, authData);

        // return registerResult
        return new RegisterResult(authToken, userData.username());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        // Get User info
        UserData userData = userDAO.getUser(loginRequest.username());
        // If null, throw error
        if (userData == null) {
            throw new DataAccessException(404, "This person doesn't exist");
        }

        // check password
        // if wrong password, send error response
        if (!Objects.equals(loginRequest.password(), userData.password())) {
            throw new DataAccessException(401, "Unauthorized");
        }
        // if correct password, create authdata
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());
        //add authData to database
        authDAO.createAuth(authToken, authData);
        // return username and authtoken
        LoginResult loginResult = new LoginResult(userData.username(), authToken);
        return loginResult;
    }

    public void deleteAllUsers() {
        userDAO.deleteAllUsers();
    }

    public void deleteAllAuthData() {
        authDAO.deleteAllAuthData();
    }
}
