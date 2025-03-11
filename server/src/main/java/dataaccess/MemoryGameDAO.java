package dataaccess;

import chess.ChessGame;
import model.CreateGameResult;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    int latestID = 1234;
    HashMap<Integer, GameData> gameList = new HashMap<>();

    public void createGame(int gameID, GameData gameData) throws DataAccessException {
        this.gameList.put(gameID, gameData);
    }

    public Collection<GameData> getAll() {
        return this.gameList.values();
    }


    public GameData getGame(int gameID) {
        return this.gameList.get(gameID);
    }

    public void updateGame(GameData newGameData) {
        this.gameList.put(newGameData.gameID(), newGameData);
    }

    public void deleteAll() throws DataAccessException {
        this.gameList = new HashMap<>();
    }


}
