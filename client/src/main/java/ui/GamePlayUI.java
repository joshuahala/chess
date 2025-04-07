package ui;

import exception.ResponseException;
import sharedserver.ServerFacade;

public class GamePlayUI implements ClientUI{
    public String authToken = "";
    private ServerFacade server = new ServerFacade(8080);
    public GamePlayUI(String authToken) {
        this.authToken = authToken;
    }

    public ClientResult eval(String line) throws ResponseException {
        var args = line.split(" ");
        switch(args[0]) {
            case "help" -> {
                return help();
            }
            default -> {
                return defaultResponse();
            }
        }
    }

    private ClientResult help() {
        var text = "" + EscapeSequences.SET_TEXT_COLOR_BLUE +
                "help - Displays text informing the user what actions they can take%n" +
                "redraw - Redraws the chess board upon the user’s request%n" +
                "leave - Removes the user from the game and returns to the Post-Login UI%n" +
                "move <FROM> <TO> - Makes a move from one square to another (e.g., move e2 e4)%n" +
                "resign - Prompts for confirmation and forfeits the game if confirmed%n" +
                "highlight <SQUARE> - Highlights legal moves for the piece at the given square (e.g., highlight e2)";
        return new ClientResult(ClientType.GAMEPLAY, "", text);
    }

    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.GAMEPLAY,"", "Please enter a valid command. Type help to see list of commands.");
    }
}
