package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    public GameData game;
    public String username;
    public String team;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);  // Call the parent constructor with the correct type
        this.game = game;
    }
    @Override
    public GameData getGame() {
        return this.game;
    }
    @Override
    public String getUsername() {
        return this.username;
    }
    @Override
    public String getTeam() {
        return this.team;
    }
}
