package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.util.Objects;
import java.util.Scanner;

public class REPL {

ClientUI preLoginClient = new PreLoginUI("");
ClientUI postLoginClient;
ClientUI client = preLoginClient;
ClientType clientType;
String authToken = "";

    public REPL() {
        this.client = new PreLoginUI("");
        this.clientType = ClientType.PRELOGIN;
    }

    public void run() {
        System.out.printf("Welcome to CS 240 Chess! Type help to get started.%n" +
                EscapeSequences.SET_TEXT_COLOR_GREEN + ">>>" + EscapeSequences.SET_TEXT_COLOR_WHITE);
        Scanner scanner = new Scanner(System.in);
        ClientResult result = new ClientResult(ClientType.PRELOGIN, "", "");
        while (!Objects.equals(result.result(), "quit")) {
            String line = scanner.nextLine();
            try {

                result = client.eval(line);
                manageClients(result);

//                System.out.printf("" + this.clientType + "%n");
                System.out.printf("" + result.result() + EscapeSequences.SET_TEXT_COLOR_GREEN + "%n>>>" + EscapeSequences.SET_TEXT_COLOR_WHITE);
            } catch (Exception exception) {
                System.out.printf("Please try again");
            }


        }
    }
    private void manageClients(ClientResult result) {
        try {
            if (result.type() != clientType) {
                this.clientType = result.type();
                switch (result.type()) {
                    case ClientType.PRELOGIN -> this.client = new PreLoginUI(result.authToken());
                    case ClientType.POSTLOGIN -> this.client = new PostLoginUI(result.authToken());
                    case ClientType.GAMEPLAY -> this.client = new GamePlayUI(result.authToken());
                }
            }

        } catch (Exception error) {
            System.out.println("An error occurred");
        }
    }
}
