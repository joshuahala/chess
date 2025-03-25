package ui;

import java.util.Objects;
import java.util.Scanner;

public class PreLoginUI implements ClientUI {
    public ClientResult eval(String arg) {
        switch (arg) {
            case "login" -> {
                return new ClientResult(ClientType.POSTLOGIN, arg);
            }
            default -> {
                return new ClientResult(ClientType.PRELOGIN, arg);
            }
        }

    }
}
