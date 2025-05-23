package ui;

import chess.*;
import exception.ResponseException;
import model.GameData;
import sharedserver.ServerFacade;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;

import java.util.Collection;

public class GamePlayUI implements ClientUI, WsObserver{
    public String authToken;
    public int gameID;
    public String team = "white";
    public Cache cache = new Cache();
    private ServerFacade server = new ServerFacade(8080);
    private WebSocketFacade ws;
    public GameData gameData = new GameData(0, null, null, "", null, "false");
    private boolean waitingForLeaveConfirmation = false;

    public GamePlayUI(Cache cache) {
        try {
            this.authToken = cache.authToken;
            this.gameID = cache.gameID;
            this.gameData = cache.gameData;
            this.cache = cache;
            this.team = cache.team;


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
            case "yes" -> {
                if (waitingForLeaveConfirmation && line.equals("yes")) {
                    return confirmLeave();
                }
                return defaultResponse();
            }
            case "resign" -> {
                return resign();
            }
            case "move" -> {
                return move(args[1], args[2]);
            }
            case "highlight" -> {
                return highLight(args[1]);
            }
            default -> {
                if (waitingForLeaveConfirmation) {
                    waitingForLeaveConfirmation = false;
                    return new ClientResult(ClientType.GAMEPLAY, cache, "Leave cancelled.");
                }
                return defaultResponse();
            }
        }
    }

    private ClientResult help() {
        var text = "" + EscapeSequences.SET_TEXT_COLOR_BLUE +
                "help - Displays text informing the user what actions they can take%n" +
                "redraw - Redraws the chess board%n" +
                "leave - Causes you to leave the game. Does not end the game.%n" +
                "move <FROM> <TO> - Makes a move from one square to another (e.g., move e2 e4)%n" +
                "resign - Choose to end the game.%n" +
                "highlight <SQUARE> - Highlights legal moves for the piece at the given square (e.g., highlight e2)";
        return new ClientResult(ClientType.GAMEPLAY, cache,  text);
    }

    private ClientResult redraw() {
        BoardPrinter boardPrinter = new BoardPrinter(this.cache.team);
        boardPrinter.print(this.gameData);
        return new ClientResult(ClientType.GAMEPLAY, cache,"");
    }

    private ClientResult leave(){
        waitingForLeaveConfirmation = true;
        return new ClientResult(ClientType.GAMEPLAY, cache, "Are you sure you want to leave the game? (yes/no)");
    }

    private ClientResult confirmLeave() {
        waitingForLeaveConfirmation = false;
        cache.authToken = authToken;
        ClientResult result = new ClientResult(ClientType.GAMEPLAY, cache, "An error occurred while trying to leave.");
        try {
            this.ws = new WebSocketFacade(new GamePlayUI(cache));
            ws.leave(authToken, gameID, cache.participantType);
            result = new ClientResult(ClientType.POSTLOGIN, cache, "");
        } catch (Exception ex) {
            System.out.println("leave error");
        }
        return result;
    }

    private ClientResult resign() {
        ws.resign(authToken, gameID, cache.participantType);
        return new ClientResult(ClientType.GAMEPLAY, this.cache, "");
    }

    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.GAMEPLAY, cache, "Please enter a valid command. Type help to see list of commands.");
    }

    private void loadGame(ServerMessage serverMessage) {
        this.gameData = serverMessage.getGame();


        BoardPrinter boardPrinter = new BoardPrinter(this.cache.team);
        boardPrinter.print(serverMessage.getGame());
        if (this.gameData.gameOver() == "true") {
            System.out.printf("That's the game!%n");
        }
    }

    private ClientResult move(String from, String to) {
        int[] fromPos = parseChessPosition(from);
        int[] toPos = parseChessPosition(to);
        ChessPosition fromPosition = new ChessPosition(fromPos[1], fromPos[0]);
        ChessPosition toPosition = new ChessPosition(toPos[1], toPos[0]);
        ChessMove move = new ChessMove(fromPosition, toPosition, null);
        MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move, team);
        System.out.println("sending through websocket");
        ws.makeMove(moveCommand);
        return new ClientResult(ClientType.GAMEPLAY, this.cache, "");
    }

    private ClientResult highLight(String position) {
        int[] parsedPosition = parseChessPosition(position);
        ChessPosition chessPosition = new ChessPosition(parsedPosition[1], parsedPosition[0]);
        ChessBoard board = this.gameData.game().getBoard();
        MovesCalculator movesCalculator = new MovesCalculator(board, chessPosition);
        ChessPiece piece = board.getPiece(chessPosition);
        Collection<ChessMove> possibleMoves = movesCalculator.possibleMoves(piece.getPieceType(), piece.getTeamColor());
        BoardPrinter boardPrinter = new BoardPrinter(this.cache.team);
        boardPrinter.highLight(possibleMoves, gameData);
        return new ClientResult(ClientType.GAMEPLAY, this.cache, "");
    }
    private int[] parseChessPosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }

        char file = pos.charAt(0); // e.g. 'e'
        char rank = pos.charAt(1); // e.g. '4'

        int col = file - 'a' + 1; // 'a' = 1, 'b' = 2, ..., 'h' = 8
        int row = Character.getNumericValue(rank); // '1' = 1, ..., '8' = 8

        return new int[]{col, row};
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
