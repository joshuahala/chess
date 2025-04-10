package ui;

import exception.ResponseException;
import model.GameData;

public interface ClientUI {
    ClientResult eval(String arg) throws ResponseException;
    void setGameData(GameData gameData);
    void initWs();
}
