package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.util.Objects;
import java.util.Scanner;

public class REPL {

ClientUI client;

    public REPL() {
        this.client = new PreLoginUI();
    }

    public void run() {
        System.out.printf("Type something...%n>>>");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            result = client.eval(line);

            System.out.printf("You typed: " + result + "%n");


        }
    }
}
