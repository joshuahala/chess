package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.util.Objects;
import java.util.Scanner;

public class REPL {

ClientUI client;
ClientType clientType;

    public REPL() {
        this.client = new PreLoginUI();
        this.clientType = ClientType.PRELOGIN;
    }

    public void run() {
        System.out.printf("Welcome to CS 240 Chess! Type help to get started.%n>>>");
        Scanner scanner = new Scanner(System.in);
        ClientResult result = new ClientResult(ClientType.PRELOGIN, "");
        while (!Objects.equals(result.result(), "quit")) {
            String line = scanner.nextLine();

            result = client.eval(line);
            manageClients(result.type());

            System.out.printf("" + this.clientType + "%n");
            System.out.printf("" + result.result() + "%n>>>");


        }
    }
    private void manageClients(ClientType type) {
        this.clientType = type;
        switch (type) {
            case ClientType.PRELOGIN -> this.client = new PreLoginUI();
            case ClientType.POSTLOGIN -> this.client = new PostLoginUI();
            default -> this.client = new PreLoginUI();
        }
    }
}
