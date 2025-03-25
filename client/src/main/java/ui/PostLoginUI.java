package ui;

import exception.ResponseException;
import model.LogoutRequest;
import model.LogoutResult;
import sharedserver.ServerFacade;

public class PostLoginUI implements ClientUI{

    public String authToken = "";

    public PostLoginUI(String authToken) {
        this.authToken = authToken;
    }
    ServerFacade server = new ServerFacade(8080);
    public ClientResult eval(String arg) throws ResponseException {
        switch (arg) {
            case "help" -> {
                return help();
            }
            case "logout" -> {
                return logout();
            }
            default -> {
                return defaultResponse();
            }
        }
    }

    private ClientResult logout() throws ResponseException {
        try {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult logoutResult = server.logout(logoutRequest);
            return new ClientResult(ClientType.PRELOGIN, "", "You have logged out");

        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", "There was an error");
        }
    }
    private ClientResult help() {
        try {
            var text ="" + EscapeSequences.SET_TEXT_COLOR_BLUE + "create <NAME> - a game%n" +
                    "list - games%n" +
                    "join <ID> [WHITE|BLACK] - a game%n" +
                    "observe <ID> - a game%n" +
                    "logout - when you are done%n" +
                    "quit - playing chess%n" +
                    "help - with possible commands%n";
            return new ClientResult(ClientType.POSTLOGIN, "", text);
        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", "" + error);
        }

    }
    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.POSTLOGIN,"", "type something real punk");
    }
}
