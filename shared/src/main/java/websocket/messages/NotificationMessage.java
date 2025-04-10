package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);  // Call the parent constructor with the correct type
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}
