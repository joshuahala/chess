package websocket.commands;

import chess.ChessMove;
import websocket.messages.ServerMessage;

public class MakeMoveCommand extends UserGameCommand{

    public ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);  // Call the parent constructor with the correct type
        this.move = move;
    }
}
