package ui;

import model.GameData;
import websocket.messages.ServerMessage;

public interface WsObserver {
   void handleMessage(ServerMessage serverMessage);
}
