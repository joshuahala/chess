package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class BoardPrinter {
    private ArrayList<String> letters = new ArrayList<>(Arrays.asList(" a ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", " h "));
    private String[] pieces = {
            " R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R ",  // Rank 1 (White's major pieces)
            " P ", " P ", " P ", " P ", " P ", " P ", " P ", " P ",  // Rank 2 (White's pawns)
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",  // Rank 3 (Empty)
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",  // Rank 4 (Empty)
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",  // Rank 5 (Empty)
            "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ",  // Rank 6 (Empty)
            " P ", " P ", " P ", " P ", " P ", " P ", " P ", " P ",  // Rank 7 (Black's pawns)
            " R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R "   // Rank 8 (Black's major pieces)
    };
    Iterator<String> generator = Arrays.stream(pieces).iterator();

    PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private int row;
    private int col;

    public BoardPrinter() {
        this.row = 0;
        this.col = 0;
    }

    public void print() {
        boardText();
    }
    private void boardText() {

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col ++) {
                var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
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
            out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        } else if (row > 4 && row < 9 && col > 0 && col < 9) {
            out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        } else {
            out.print(EscapeSequences.RESET_TEXT_COLOR);
        }
//        if (row > 0 && row < 4 && col > 0 && col < 9) {
//            out.print(EscapeSequences.SET_TEXT_COLOR_RED);
//        } else if (row > 4 && row < 9 && col > 0 && col < 9) {
//            out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
//        }
    }
    private void printContent(int row, int col) {

        if (col == 0 || col == 9) {
            if (row != 0 && row != 9) {
                out.print(" " + row + " ");
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
