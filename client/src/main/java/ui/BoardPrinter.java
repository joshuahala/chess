package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class BoardPrinter {
    private static String PLAYER_COLOR = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static String OPPONENT_COLOR = EscapeSequences.SET_TEXT_COLOR_RED;
    private ArrayList<String> lettersWhite = new ArrayList<>(Arrays.asList(" a ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", " h "));
    private ArrayList<String> lettersBlack = new ArrayList<>(Arrays.asList(" h ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", " a "));
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
    private String team = "white";
    private ArrayList<String> letters = lettersWhite;
    private String[] pieces = piecesWhite;
    Iterator<String> generator;

    public BoardPrinter(String team) {
        this.team = team.toLowerCase();
        this.row = 0;
        this.col = 0;
        if ("black".equals(team)) {
            this.letters = lettersBlack;
            this.pieces = piecesBlack;
            this.PLAYER_COLOR = EscapeSequences.SET_TEXT_COLOR_RED;
            this.OPPONENT_COLOR = EscapeSequences.SET_TEXT_COLOR_BLUE;
        }
        generator  = Arrays.stream(pieces).iterator();
    }

    public void print() {
        boardText();
    }
    private void boardText() {

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
    }

    private void setBgColor(int row, int col) {
        if (row == 0 || row == 9 || col == 0 || col == 9) {
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else {
            if ((row + col) % 2 == 0) {
                out.print(EscapeSequences.SET_BG_COLOR_WHITE);

            } else {
                out.print(EscapeSequences.SET_BG_COLOR_BLACK);

            }
        }
    }
    private void setTextColor(int row, int col) {
        if (row > 0 && row < 3 && col > 0 && col < 9) {
            out.print(OPPONENT_COLOR);
        } else if (row > 4 && row < 9 && col > 0 && col < 9) {
            out.print(PLAYER_COLOR);
        } else {
            out.print(EscapeSequences.RESET_TEXT_COLOR);
        }
    }
    private void printContent(int row, int col) {

        if (col == 0 || col == 9) {
            if (row != 0 && row != 9) {
                out.print(" " + (10-(row+1)) + " ");
            } else {
                out.print("   ");
            }

        } else if (row == 0 || row == 9) {
            if (col != 0 && col != 9) {
                out.print(letters.get(col));
            }
        } else if (col > 0 && col < 9 && row > 0 && row < 9) {
            out.print(generator.next());
        }
    }
}
