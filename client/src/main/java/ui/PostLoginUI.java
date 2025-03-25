package ui;

public class PostLoginUI implements ClientUI{
    public ClientResult eval(String arg) {
        switch (arg) {
            case "logout" -> {
                return new ClientResult(ClientType.PRELOGIN, arg);
            }
            default -> {
                return new ClientResult(ClientType.POSTLOGIN, arg);
            }
        }
    }
}
