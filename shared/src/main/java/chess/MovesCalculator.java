package chess;

import java.util.ArrayList;


public class MovesCalculator {
    ChessBoard board;
    ChessPosition position;
    ChessPiece.PieceType piece;
    ChessGame.TeamColor color;

    public MovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;

    }

    public ArrayList<ChessMove> PossibleMoves(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        this.color = color;
        switch(type) {
//            case PAWN -> {
//                return this.PawnMoves();
//            }
//            case ROOK -> {
//                return this.RookMoves();
//            }
//            case KNIGHT -> {
//                return this.KnightMoves();
//            }
            case BISHOP -> {
                return this.BishopMoves();
            }
//            case QUEEN -> {
//                return this.QueenMoves();
//            }
//            case KING -> {
//                return this.KingMoves();
//            }
            default -> throw new IllegalArgumentException("Invalid piece type: " + type);
        }
    }

    public ArrayList<ChessMove> BishopMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition startPosition = new ChessPosition(row, col);
        while (row > 1 && col > 1) {
            row --;
            col --;
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(startPosition, newPosition, null);
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.color) {
                    moves.add(move);
                    break;
                } else {
                    break;
                }
            }
            moves.add(move);

        }

        row = position.getRow();
        col = position.getColumn();

        while (row < 8 && col > 1) {
            row ++;
            col --;
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(startPosition, newPosition, null);
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.color) {
                    moves.add(move);
                    break;
                } else {
                    break;
                }
            }
            moves.add(move);
        }

        row = position.getRow();
        col = position.getColumn();

        while (row < 8 && col < 8) {
            row ++;
            col ++;
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(startPosition, newPosition, null);
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.color) {
                    moves.add(move);
                    break;
                } else {
                    break;
                }
            }
            moves.add(move);
        }

        row = position.getRow();
        col = position.getColumn();

        while (row > 1 && col < 8) {
            row --;
            col ++;
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(startPosition, newPosition, null);
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.color) {
                    moves.add(move);
                    break;
                } else {
                    break;
                }
            }
            moves.add(move);
        }

        return moves;
    }
}
