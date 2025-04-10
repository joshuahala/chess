package ui;

import exception.ResponseException;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;
import sharedserver.ServerFacade;

import java.util.Objects;
import java.util.Scanner;

public class PreLoginUI implements ClientUI {
    private ServerFacade server = new ServerFacade(8080);
    public String authToken = "";
    Cache cache = new Cache();
    Cache emptyCache = new Cache();


    public PreLoginUI(String authToken) {
        this.authToken = authToken;
    }
    public ClientResult eval(String line) throws ResponseException{
        var args = line.split(" ");
        switch (args[0]) {
            case "help" -> {
                return help();
            }
            case "quit" -> {
                return quit();
            }
            case "login" -> {
                return login(args);
            }
            case "register" -> {
                return register(args);
            }
            default -> {
                return defaultResponse();
            }
        }

    }

    private ClientResult help() {
        var text ="" + EscapeSequences.SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account%n" +
                "login <USERNAME> <PASSWORD> - to play chess%n" +
                "quit - playing chess%n" +
                "help - with possible commands";
        return new ClientResult(ClientType.PRELOGIN, cache, text);
    }

    private ClientResult register(String[] args) throws ResponseException {
        try {
            if (args.length != 4) {
                return new ClientResult(ClientType.PRELOGIN, cache, "Invalid number of command arguments. Type help to see available commands.");
            }
            // create user and register
            UserData userData = new UserData(args[1], args[2], args[3]);
            RegisterResult registerResult = server.register(userData);
            // login user
            LoginRequest loginRequest = new LoginRequest(userData.username(), userData.password());
            LoginResult loginResult = server.login(loginRequest);
            cache.authToken = loginResult.authToken();
            return new ClientResult(ClientType.POSTLOGIN, cache, "You have logged in. Type help to see available commands.");
        } catch (Exception error) {
            return new ClientResult(ClientType.PRELOGIN,cache,"There was an error");
        }
    }

    private ClientResult login(String[] args) throws ResponseException {
        try {
            if (args.length != 3) {
                return new ClientResult(ClientType.PRELOGIN, cache, "Invalid number of command arguments. Type help to see available commands.");
            }
            LoginRequest loginRequest = new LoginRequest(args[1], args[2]);
            LoginResult loginResult = server.login(loginRequest);
            cache.authToken = loginResult.authToken();
            return new ClientResult(ClientType.POSTLOGIN, cache, "You have logged in. Type help to see available commands.");
        } catch (Exception error) {
            return new ClientResult(ClientType.PRELOGIN, cache, "An error occurred. Please try a different name or password.");
        }

    }

    private ClientResult quit() throws ResponseException {
        return new ClientResult(ClientType.PRELOGIN, cache, "quit");
    }

    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.PRELOGIN,emptyCache,"Please enter a valid command. Type help to see list of commands.");
    }
}
