package chess;

import java.util.ArrayList;


public class MovesCalculator {
    ChessBoard board;
    ChessPosition position;
    ChessPiece.PieceType piece;

    public MovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;

    }

    public ArrayList<ChessMove> bishopMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition startPosition = new ChessPosition(row, col);
        while (row <= 8 || col <= 8) {

            row += 1;
            col += 1;
            ChessPosition endPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP);
            moves.add(move);
        }
        return moves;
    }
}
