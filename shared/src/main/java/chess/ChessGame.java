package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard gameBoard;
    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        this.gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (this.gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        ChessGame.TeamColor teamColor = this.gameBoard.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> pieceMoves = this.gameBoard.getPiece(startPosition).pieceMoves(this.gameBoard,startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove possibleMove : pieceMoves) {
            ChessBoard boardCopy = new ChessBoard(this.gameBoard);
            ChessPosition endPosition = possibleMove.getEndPosition();
            boardCopy.squares[endPosition.getRow()-1][endPosition.getColumn()-1] = this.gameBoard.getPiece(startPosition);
            boardCopy.squares[startPosition.getRow()-1][startPosition.getColumn()-1] = null;

            boolean result = this.checkForCheck(boardCopy, teamColor);
            if (!result) {
                validMoves.add(possibleMove);
            }
        }
        return validMoves;
    }

    public boolean checkForCheck(ChessBoard board, TeamColor teamColor) {
        // for each of the opponent's pieces
        //    if any one of the piece's possible moves is the same
        //    as the king's position, then return true
        boolean result = false;
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row ++) {
            for (int col =1; col <= 8; col ++) {
                ChessPosition iteratedPosition = new ChessPosition(row, col);
                ChessPiece iteratedPiece = board.getPiece(iteratedPosition);

                if (iteratedPiece != null && iteratedPiece.getPieceType() == ChessPiece.PieceType.KING && iteratedPiece.getTeamColor() == teamColor) {
                    kingPosition = iteratedPosition;
                    break;
                }
            }
        }
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition iteratedPosition = new ChessPosition(row, col);
                ChessPiece iteratedPiece = board.getPiece(iteratedPosition);
                if (iteratedPiece != null) {
                    Collection<ChessMove> pieceMoves = iteratedPiece.pieceMoves(board, iteratedPosition);
                    for (ChessMove possibleMove : pieceMoves) {
                        ChessPosition possibleEndPosition = possibleMove.getEndPosition();
                        if (kingPosition != null && possibleEndPosition.thisRow == kingPosition.thisRow && possibleEndPosition.thisCol == kingPosition.thisCol) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = this.validMoves(move.getStartPosition());
        ChessPiece piece = this.gameBoard.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            piece.type = move.getPromotionPiece();
        }
        if (validMoves != null && piece.getTeamColor() == teamTurn && validMoves.contains(move)) {
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            this.gameBoard.addPiece(endPos,piece);
            this.gameBoard.squares[startPos.getRow()-1][startPos.getColumn()-1] = null;
            if (teamTurn == TeamColor.BLACK) {
                teamTurn = TeamColor.WHITE;
            } else {
                teamTurn = TeamColor.BLACK;
            }
        } else {
            throw new InvalidMoveException("Not a valid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return this.checkForCheck(this.gameBoard, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ArrayList<ChessPosition> threateningPositions = new ArrayList<>();
        ChessPosition kingPosition = null;
        ArrayList<ChessPosition> enemyMoves = new ArrayList<>();
        // check if is in check
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (int row = 1; row <= 8; row ++) {
            for (int col =1; col <= 8; col ++) {
                ChessPosition iteratedPosition = new ChessPosition(row, col);
                ChessPiece iteratedPiece = this.gameBoard.getPiece(iteratedPosition);
                if (iteratedPiece != null && iteratedPiece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> validMoves = this.validMoves(iteratedPosition);
                if (validMoves != null && validMoves.size() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (this.isInCheckmate(teamColor)) {
            return false;
        }
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition iteratedPosition = new ChessPosition(row, col);
                ChessPiece iteratedPiece = this.gameBoard.getPiece(iteratedPosition);
                if (iteratedPiece != null && iteratedPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> pieceMoves = this.validMoves(iteratedPosition);
                    if (pieceMoves != null && pieceMoves.size() > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }
}
