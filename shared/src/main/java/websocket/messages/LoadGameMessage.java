package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    public GameData game;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);  // Call the parent constructor with the correct type
        this.game = game;
    }
    public GameData getGame() {
        return this.game;
    }
}
