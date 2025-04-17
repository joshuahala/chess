package websocket.messages;

public class ErrorMessage extends ServerMessage {
    public String errorMessage;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);  // Call the parent constructor with the correct type
        this.errorMessage = message;
    }
    public String getMessage() {
        return this.errorMessage;
    }
}
