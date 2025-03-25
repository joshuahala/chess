package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class BoardPrinter {
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
    private void printContent(int row, int col) {
        if (col == 0 || col == 9) {
            if (row != 0 && row != 9) {
                out.print(" " + row + " ");
            } else {
                out.print("   ");
            }
        } else {
            out.print("   ");
        }
    }
}
