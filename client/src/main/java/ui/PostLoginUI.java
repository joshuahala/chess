package ui;

import exception.ResponseException;
import model.*;
import sharedserver.ServerFacade;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class PostLoginUI implements ClientUI{

    WebSocketFacade ws;

    public String authToken = "";
    private ArrayList<GameDataWithoutGames> gamesArray = new ArrayList<>();
    public PostLoginUI(String authToken) {
        this.authToken = authToken;
        try {
            initGamesList();
            ws = new WebSocketFacade();
        } catch (Exception error) {
            System.out.printf("" + error);
        }
    }
    ServerFacade server = new ServerFacade(8080);

    public ClientResult eval(String line) throws ResponseException {
        var args = line.split(" ");
        switch (args[0]) {
            case "help" -> {
                return help();
            }
            case "logout" -> {
                return logout();
            }
            case "create" -> {
                return create(args);
            }
            case "list" -> {
                return listGames();
            }
            case "join" -> {
                return join(args);
            }
            case "observe" -> {
                return observe(args);
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
            return new ClientResult(ClientType.PRELOGIN, "", 0, "You have logged out. Type help to see available commands.");

        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", 0, "There was an error");
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
            return new ClientResult(ClientType.POSTLOGIN, "", 0, text);
        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", 0,  "" + error);
        }
    }
    private ClientResult create(String[] args) throws ResponseException {
        try {
            if (args.length != 2) {
                return new ClientResult(ClientType.POSTLOGIN, "", 0, "Invalid number of command arguments. Type help to see available commands.");
            }
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, args[1]);
            server.createGame(createGameRequest);
            return new ClientResult(ClientType.POSTLOGIN, "", 0, "you created a game!");
        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", 0, "" + error);
        }
    }

    private ClientResult listGames() throws ResponseException {
        try {
            // get games
            ListGamesResult listResult = server.listGames(authToken);
            var text = "Games:%n";
            var whiteName = "__";
            var blackName = "__";
            Collection<GameDataWithoutGames> games = listResult.games();
            gamesArray = new ArrayList<>(games);
            // create games list text for output
            for (GameDataWithoutGames game : gamesArray) {
                int index = gamesArray.indexOf(game) + 1;
                whiteName = game.whiteUsername() != null? game.whiteUsername() : "";
                blackName = game.blackUsername() != null? game.blackUsername() : "";
                text = text + "#" + Integer.toString(index) + " Game Name: " + game.gameName() + "   White: " +
                        whiteName + " " + "  Black: " + blackName + " " +   "%n";
            }
            return new ClientResult(ClientType.POSTLOGIN, "", 0, text);

        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "",0, "" + error);
        }
    }

    private ClientResult join(String[] args) throws ResponseException {
        try {
            if (args.length != 3) {
                return new ClientResult(ClientType.POSTLOGIN, "", 0, "Invalid number of command arguments. Type help to see available commands.");
            }
            String indexString = args[1];
            int indexInt = -1;
            try {
                indexInt = (Integer.parseInt(indexString) - 1);
            } catch (Exception error) {
                return new ClientResult(ClientType.POSTLOGIN, "", 0, "Please enter a valid game id");
            }
            if (indexInt < 0 || indexInt > gamesArray.size() - 1) {
                return new ClientResult(ClientType.POSTLOGIN, "",0, "No such game exists");
            }
            if (!Objects.equals(args[2], "white") && !Objects.equals(args[2], "black")) {
                return new ClientResult(ClientType.POSTLOGIN, "",0, "Please enter a valid team color; eg. white or black");
            }

            try {
                int gameIndex = Integer.parseInt(args[1]) - 1;
                int gameID = gamesArray.get(gameIndex).gameID();
                String playerColor = args[2];
                JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, Integer.toString(gameID));
                server.joinGame(joinGameRequest, authToken);
                BoardPrinter boardPrinter = new BoardPrinter(args[2]);
                boardPrinter.print();
                ws.connect(authToken, gameID);
                return new ClientResult(ClientType.GAMEPLAY, authToken,gameID, "You have joined game ");
            } catch (Exception error) {
                return new ClientResult(ClientType.POSTLOGIN, "",0, "This game slot is already taken. ");
            }

        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "",0, "" + error);
        }
    }
    private ClientResult observe(String[] args) throws ResponseException {
        if (args.length != 2) {
            return new ClientResult(ClientType.POSTLOGIN, "", 0, "Invalid number of command arguments. Type help to see available commands.");
        }
        String indexString = args[1];
        int indexInt = -1;
        try {
            indexInt = (Integer.parseInt(indexString) - 1);
        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", 0, "Please enter a valid game id");
        }
        if (indexInt < 0 || indexInt > gamesArray.size() - 1) {
            return new ClientResult(ClientType.POSTLOGIN, "",0, "No such game exists");
        }
        try {
            int gameIndex = Integer.parseInt(args[1]) - 1;
            int gameID = gamesArray.get(gameIndex).gameID();
            BoardPrinter boardPrinter = new BoardPrinter("white");
            boardPrinter.print();
            ws.connect(authToken, gameID);
            return new ClientResult(ClientType.POSTLOGIN, "", 0,"");
        } catch (Exception error) {
            return new ClientResult(ClientType.POSTLOGIN, "", 0,"" + error);
        }
    }
    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.POSTLOGIN,"", 0, "Please enter a valid command. Type help to see list of commands.");
    }

    private void initGamesList() throws ResponseException {
        try {
            ListGamesResult listResult = server.listGames(this.authToken);
            Collection<GameDataWithoutGames> games = listResult.games();
            gamesArray = new ArrayList<>(games);
        } catch (Exception error) {
            System.out.println("An error occurred while trying to fetch games.");
        }

    }
}
