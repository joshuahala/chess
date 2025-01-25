package chess;

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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // for each of the opponent's pieces
        //    if any one of the piece's possible moves is the same
        //    as the king's position, then return true
        Boolean result = false;
        ChessPosition kingPosition = null;
        for (int row = 1; row < 8; row ++) {
            for (int col =1; col < 8; col ++) {
                ChessPosition iteratedPosition = new ChessPosition(row, col);
                ChessPiece iteratedPiece = this.gameBoard.getPiece(iteratedPosition);
                if (iteratedPiece != null && iteratedPiece.getPieceType() == ChessPiece.PieceType.KING && iteratedPiece.getTeamColor() == teamColor) {
                    kingPosition = iteratedPosition;
                    break;
                }
            }
        }
        for (int row = 1; row < 8; row ++) {
            for (int col = 1; col < 8; col++) {
                ChessPosition iteratedPosition = new ChessPosition(row, col);
                ChessPiece iteratedPiece = this.gameBoard.getPiece(iteratedPosition);
                if (iteratedPiece != null) {
                    Collection<ChessMove> pieceMoves = iteratedPiece.pieceMoves(this.gameBoard, iteratedPosition);
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
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
