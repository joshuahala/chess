package ui;

import java.util.Objects;
import java.util.Scanner;

public class PreLoginUI implements ClientUI {
    public ClientResult eval(String arg) {
        switch (arg) {
            case "help" -> {
                return help();
            }
//            case "quit" -> quit();
//            case "login" -> login();
//            case "register" -> register();
            default -> {
                return defaultResponse();
            }
        }

    }

    private ClientResult help() {
        var text = "register <USERNAME> <PASSWORD> <EMAIL> - to create an account%n" +
                "login <USERNAME> <PASSWORD> - to play chess%n" +
                "quit - playing chess" +
                "help - with possible commands";
        return new ClientResult(ClientType.PRELOGIN, text);
    }

    private ClientResult defaultResponse() {
        return new ClientResult(ClientType.PRELOGIN, "type something real punk");
    }
}
