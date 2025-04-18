package ui;

import chess.*;
import com.google.gson.Gson;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BoardPrinter {
    private static String playerColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static String opponentColor = EscapeSequences.SET_TEXT_COLOR_RED;
    private ArrayList<String> lettersWhite = new ArrayList<>(Arrays.asList("   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "));
    private ArrayList<String> lettersBlack = new ArrayList<>(Arrays.asList("   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "));
    private String[] piecesWhite = {
            " R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R ",
            " P ", " P ", " P ", " P ", " P ", " P ", " P ", " P ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            " P ", " P ", " P ", " P ", " P ", " P ", " P ", " P ",
            " R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R "
    };
    private String[] piecesBlack = {
            " R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R ",
            " P ", " P ", " P ", " P ", " P ", " P ", " P ", " P ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",
            " P ", " P ", " P ", " P ", " P ", " P ", " P ", " P ",
            " R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "
    };

    PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private int row;
    private int col;
    private String team = "black";
    private GameData gameData;
    private ArrayList<ChessPosition> possibleMoves;
    private ArrayList<String> letters = lettersWhite;
    private String[] pieces = piecesWhite;
    private ChessPosition startPos;
    Iterator<String> generator;

    ChessBoard theBoard;

    public BoardPrinter(String team) {

        this.team = team.toLowerCase();
        this.row = 0;
        this.col = 0;

        if ("black".equals(team)) {
            this.letters = lettersBlack;
            this.pieces = piecesBlack;
            this.playerColor = EscapeSequences.SET_TEXT_COLOR_RED;
            this.opponentColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
        }
        generator  = Arrays.stream(pieces).iterator();
    }

    public void highLight(Collection<ChessMove> possibleMoves, GameData gameData) {
        ArrayList<ChessPosition> highlightedPositions = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            highlightedPositions.add(move.getEndPosition());
            this.startPos = move.getStartPosition();
        }
        this.possibleMoves = highlightedPositions;
        this.print(gameData);
    }
    public void print(GameData gameData) {
        ChessGame game = new ChessGame();
        this.gameData = gameData;
        game.setBoard(gameData.game().getBoard());
        game.setTeamTurn(gameData.game().getTeamTurn());
        theBoard = game.getBoard();

        System.out.printf("%n");
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col ++) {
                setBgColor(row,col);
                setTextColor(row,col);
                printContent(row, col);
            }
            out.print(EscapeSequences.RESET_BG_COLOR);
            out.printf("%n");
        }

        out.print(EscapeSequences.RESET_BG_COLOR);
        out.printf(EscapeSequences.SET_TEXT_COLOR_GREEN + "%n>>>" + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private void setBgColor(int row, int col) {
        if (row == 0 || row == 9 || col == 0 || col == 9) {
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else {

            if (Objects.equals(this.team, "white")) {
                if (this.startPos != null && ((this.startPos.getRow()) == (9 - row)) && ((this.startPos.getColumn()) == (col))) {
                    out.print(EscapeSequences.SET_BG_COLOR_YELLOW);

                } else if ((row + col) % 2 == 0) {
                    if (possibleMoves != null && possibleMoves.contains(new ChessPosition(9 - row, col))) {
                        out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else {
                        out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }

                } else {
                    if (possibleMoves != null && possibleMoves.contains(new ChessPosition(9 - row, col))) {
                        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    } else {
                        out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    }

                }
            } else if (Objects.equals(this.team, "black")) {
                if (this.startPos != null && ((this.startPos.getRow()) == (row)) && ((this.startPos.getColumn()) == (9 - col))) {
                    out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                } else if ((row + col) % 2 == 0) {
                    if (possibleMoves != null && possibleMoves.contains(new ChessPosition(row, 9 - col))) {
                        out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else {
                        out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }

                } else {
                    if (possibleMoves != null && possibleMoves.contains(new ChessPosition(row, 9 - col))) {
                        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    } else {
                        out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    }

                }
            }

//            if (Objects.equals(this.team, "black")) {
//                if ((row + col) % 2 == 0) {
//                    out.print(EscapeSequences.SET_BG_COLOR_BLACK);
//
//                } else {
//                    out.print(EscapeSequences.SET_BG_COLOR_WHITE);
//
//                }
//            }

        }
    }
    private void setTextColor(int row, int col) {
        if (col > 0 && col < 9 && row > 0 && row < 9) {
            // Convert our 1-8 board coordinates to 0-7 array indices
            int boardRow = Objects.equals(team, "black") ? 8 - row : row - 1;
            int boardCol = Objects.equals(team, "black") ? 8 - col : col - 1;
            
            ChessPiece piece = theBoard.getPiece(new ChessPosition(9 - (boardRow + 1), boardCol + 1));
            if (piece != null) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
                } else {
                    out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                }
            } else {
                out.print(EscapeSequences.RESET_TEXT_COLOR);
            }
        } else {
            out.print(EscapeSequences.RESET_TEXT_COLOR);
        }
    }
    private void printContent(int row, int col) {
        if (col == 0 || col == 9) {
            if (row != 0 && row != 9) {
                if (Objects.equals(team, "white")) {
                    out.print(" " + (9 - row) + " ");
                } else {
                    out.print(" " + row + " ");
                }
            } else {
                out.print("   ");
            }
        } else if (row == 0 || row == 9) {
            if (col != 0 && col != 9) {
                out.print(letters.get(col));
            }
        } else if (col > 0 && col < 9 && row > 0 && row < 9) {
            // Convert our 1-8 board coordinates to 0-7 array indices
            int boardRow = Objects.equals(team, "black") ? 8 - row : row - 1;
            int boardCol = Objects.equals(team, "black") ? 8 - col : col - 1;
            
            ChessPiece piece = theBoard.getPiece(new ChessPosition(9 - (boardRow + 1), boardCol + 1));
            if (piece != null) {
                String pieceSymbol = getPieceSymbol(piece);
                out.print(pieceSymbol);
            } else {
                out.print("   ");
            }
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        String symbol = " ";
        switch (piece.getPieceType()) {
            case KING -> symbol = "K";
            case QUEEN -> symbol = "Q";
            case BISHOP -> symbol = "B";
            case KNIGHT -> symbol = "N";
            case ROOK -> symbol = "R";
            case PAWN -> symbol = "P";
        }
        return " " + symbol + " ";
    }
}
