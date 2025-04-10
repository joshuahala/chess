package ui;

import exception.ResponseException;
import model.GameData;
import sharedserver.ServerFacade;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

public class GamePlayUI implements ClientUI, WsObserver{
    public String authToken;
    public int gameID;
    public Cache cache = new Cache();
    private ServerFacade server = new ServerFacade(8080);
    private WebSocketFacade ws;
    public GameData gameData = new GameData(0, null, null, "", null);

    public GamePlayUI(Cache cache) {
        try {
            this.authToken = cache.authToken;
            this.gameID = cache.gameID;
            this.gameData = cache.gameData;
            this.cache = cache;


//            this.ws = new WebSocketFacade(new GamePlayUI(authToken, gameID));
        } catch (Exception exception) {
            System.out.println("gameplay error:" + exception.getMessage());
        }
    }
    @Override
    public void initWs() {
        try {
            ws = new WebSocketFacade(this);
            ws.connect(authToken, gameID);
        } catch (Exception ex) {
            System.out.println("Error Initializing websocket");
        }
    }

    public ClientResult eval(String line) throws ResponseException {
        var args = line.split(" ");
        switch(args[0]) {
            case "help" -> {
                return help();
            }
            case "redraw" -> {
                return redraw();
            }
            case "leave" -> {
                return leave();
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
        return new ClientResult(ClientType.GAMEPLAY, cache,  text);
    }

    private ClientResult redraw() {
        BoardPrinter boardPrinter = new BoardPrinter(this.cache.team);
        boardPrinter.print(this.gameData);
        return new ClientResult(ClientType.GAMEPLAY, cache,"");
    }



    private ClientResult leave(){
        cache.authToken = authToken;
        ClientResult result = new ClientResult(ClientType.GAMEPLAY, cache, "An error occured while trying to leave.");
        try {
            this.ws = new WebSocketFacade(new GamePlayUI(cache));
            ws.leave(authToken, gameID);
            result = new ClientResult(ClientType.POSTLOGIN, cache, "");
        } catch (Exception ex) {
            System.out.println("leave error");
        }
        return result;
    }

    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.GAMEPLAY, cache, "Please enter a valid command. Type help to see list of commands.");
    }

    private void loadGame(ServerMessage serverMessage) {
        this.gameData = serverMessage.getGame();

        BoardPrinter boardPrinter = new BoardPrinter(serverMessage.getTeam());
        boardPrinter.print(serverMessage.getGame());
    }

    public void handleMessage(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            loadGame(serverMessage);
        }
    }

    @Override
    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }
    //    private void addConnection() {
//        ws.connect(authToken, gameID);
//    }
}
