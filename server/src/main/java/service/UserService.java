package service;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.util.log.Log;
import org.mindrot.jbcrypt.BCrypt;


import java.util.Objects;
import java.util.UUID;

public class UserService {
//    public MemoryUserDAO userDAO;
//    public MemoryAuthDAO authDAO;
    public UserDAO userDAO;
    public AuthDAO authDAO;


    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public RegisterResult register(UserData userData) throws DataAccessException {
        // ensure strings are not empty
        UserData existing = userDAO.getUser(userData.username());
        if (existing != null) {
            throw new DataAccessException(403, "unauthorized");
        }
        if (Objects.equals(userData.username(), "") || Objects.equals(newHashPassword(userData.password()), "") || Objects.equals(userData.email(), "")) {
            throw new DataAccessException(400, "bad request");
        }
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new DataAccessException(400, "bad request");
        }
//        check if user exists
//        try {
//            userDAO.getUser(userData.username());
//        } catch (Exception e) {
//            throw new DataAccessException(403,"This user already exists");
//
//        }
        //if not, then create user data
        //add userdata
        UserData newUser = new UserData(userData.username(), newHashPassword(userData.password()), userData.email());
        userDAO.createUser(newUser);
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
            throw new DataAccessException(401, "This person doesn't exist");
        }

        // check password
        // if wrong password, send error response
        if (!BCrypt.checkpw(loginRequest.password(), userData.password())) {

            throw new DataAccessException(401, String.format("expected: %s, given: %s",newHashPassword(userData.password()), newHashPassword(loginRequest.password())));
        }
        // check if user already logged in

        // if correct password, create authdata
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());
        //add authData to database
        authDAO.createAuth(authToken, authData);
        // return username and authtoken
        LoginResult loginResult = new LoginResult(userData.username(), authToken);
        return loginResult;
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        // check for authData
        if (authDAO.getAuth(logoutRequest.authToken()) == null) {
            throw new DataAccessException(401, "unauthorized");
        }
        // if authData, then delete it
        authDAO.deleteAuth(logoutRequest.authToken());
        return new LogoutResult(true);

    }

    public void deleteAllUsers() throws DataAccessException {
        userDAO.deleteAllUsers();
    }

    public void deleteAllAuthData() throws DataAccessException {
        authDAO.deleteAllAuthData();
    }

    private String newHashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


}
