package ui;

public record ClientResult(ClientType type, String authToken, int gameID, String result) {
}
