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

    private boolean isInBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    public ArrayList<ChessMove> possibleMoves(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        this.color = color;
        switch(type) {
            case PAWN -> {
                return this.pawnMoves();
            }
            case ROOK -> {
                return this.rookMoves();
            }
            case KNIGHT -> {
                return this.knightMoves();
            }
            case BISHOP -> {
                return this.bishopMoves();
            }
            case QUEEN -> {
                return this.queenMoves();
            }
            case KING -> {
                return this.kingMoves();
            }
            default -> throw new IllegalArgumentException("Invalid piece type: " + type);
        }
    }

    public ArrayList<ChessMove> bishopMoves() {
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

    public ArrayList<ChessMove> kingMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> finalList = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition startPosition = new ChessPosition(row, col);

        moves.add(new ChessMove(startPosition, new ChessPosition(row+1, col), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-1, col), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row, col+1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row, col-1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row+1, col+1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row+1, col-1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-1, col-1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-1, col+1), null));
        for (ChessMove move : moves) {
            if (!isInBounds(move.getEndPosition())) {
                continue;
            }
            if (board.getPiece(move.getEndPosition()) != null) {
                if (board.getPiece(move.getEndPosition()).getTeamColor() == this.color) {
                    continue;
                }

            }
            finalList.add(move);
        }

        return finalList;
    }

    public ArrayList<ChessMove> knightMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> finalList = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition startPosition = new ChessPosition(row, col);

        moves.add(new ChessMove(startPosition, new ChessPosition(row+2, col+1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row+1, col+2), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-2, col-1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-1, col-2), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row+1, col-2), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-1, col+2), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row-2, col+1), null));
        moves.add(new ChessMove(startPosition, new ChessPosition(row+2, col-1), null));
        for (ChessMove move : moves) {
            if (!isInBounds(move.getEndPosition())) {
                continue;
            }
            if (board.getPiece(move.getEndPosition()) != null) {
                if (board.getPiece(move.getEndPosition()).getTeamColor() == this.color) {
                    continue;
                }

            }
            finalList.add(move);
        }

        return finalList;
    }


    private boolean shouldPromote(ChessPosition newPosition) {
        if (this.color == ChessGame.TeamColor.WHITE) {
            if (newPosition.getRow() == 8) {
                return true;
            }
        }
        if (this.color == ChessGame.TeamColor.BLACK) {
            if (newPosition.getRow() == 1) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ChessMove> pawnMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> finalList = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int direction = 1;
        if (this.color == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }

        ChessPosition startPosition = new ChessPosition(row, col);
        ChessPosition newPosition;

        newPosition = new ChessPosition((row+(1*direction)), col);
        if (isInBounds(newPosition)) {
            if (board.getPiece(newPosition) == null) {
                if (shouldPromote(newPosition)) {
                    moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                }
            }
        }

        newPosition = new ChessPosition((row+(1*direction)), col-1);
        if (isInBounds(newPosition)) {
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.color) {
                    if (shouldPromote(newPosition)) {
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
        }

        newPosition = new ChessPosition((row+(1*direction)), col+1);
        if (isInBounds(newPosition)) {
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.color) {
                    if (shouldPromote(newPosition)) {
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
        }

        if (this.color == ChessGame.TeamColor.WHITE && position.getRow() == 2) {
            ChessPosition newPosition1 = new ChessPosition((row+(1*direction)), col);
            ChessPosition newPosition2 = new ChessPosition((row+(2*direction)), col);
            if (board.getPiece(newPosition1) == null && board.getPiece(newPosition2) == null) {
                moves.add(new ChessMove(startPosition, newPosition2, null));
            }
        }

        if (this.color == ChessGame.TeamColor.BLACK && position.getRow() == 7) {
            ChessPosition newPosition1 = new ChessPosition((row+(1*direction)), col);
            ChessPosition newPosition2 = new ChessPosition((row+(2*direction)), col);
            if (board.getPiece(newPosition1) == null && board.getPiece(newPosition2) == null) {
                moves.add(new ChessMove(startPosition, newPosition2, null));
            }
        }
        return moves;
    }

    public ArrayList<ChessMove> rookMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> finalList = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition startPosition = new ChessPosition(row, col);
        ChessPosition newPosition;

        for (int i = row + 1; i < 9; i ++) {
            newPosition = new ChessPosition(i, col);
            if (isInBounds(newPosition)) {
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() == this.color) {
                    break;
                } else {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                    break;
                }
            }
        }
        for (int i = row - 1; i > 0; i --) {
            newPosition = new ChessPosition(i, col);
            if (isInBounds(newPosition)) {
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() == this.color) {
                    break;
                } else {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                    break;
                }
            }
        }
        for (int j = col + 1; j < 9; j ++) {
            newPosition = new ChessPosition(row, j);
            if (isInBounds(newPosition)) {
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() == this.color) {
                    break;
                } else {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                    break;
                }
            }
        }
        for (int j = col - 1; j > 0; j --) {
            newPosition = new ChessPosition(row, j);
            if (isInBounds(newPosition)) {
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() == this.color) {
                    break;
                } else {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                    break;
                }
            }
        }
        return moves;
    }
    public ArrayList<ChessMove> queenMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> bishopMoves = bishopMoves();
        ArrayList<ChessMove> rookMoves = rookMoves();
        moves.addAll(bishopMoves);
        moves.addAll(rookMoves);

        return moves;
    }
}
