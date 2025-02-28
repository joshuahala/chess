package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.*;


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
            throw new DataAccessException("This user already exists");
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


    public void deleteAllUsers() {
        userDAO.deleteAllUsers();
    }

    public void deleteAllAuthData() {
        authDAO.deleteAllAuthData();
    }
}
