package websocket.commands;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UserGameCommandDeserializer implements JsonDeserializer<UserGameCommand> {
    @Override
    public UserGameCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String typeStr = obj.get("serverMessageType").getAsString();
        UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(typeStr);

        return switch (type) {
            case CONNECT, RESIGN, LEAVE -> context.deserialize(json, UserGameCommand.class);
            case MAKE_MOVE -> context.deserialize(json, MakeMoveCommand.class);
            default -> throw new JsonParseException("Unknown message type: " + typeStr);
        };
    }
}

