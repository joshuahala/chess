package dataaccess;

import chess.ChessGame;
import model.CreateGameResult;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    int latestID = 1234;
    HashMap<String, GameData> gameList = new HashMap<>();

    public void createGame(String gameName, GameData gameData) throws DataAccessException {
        this.gameList.put(gameName, gameData);
    }

    public Collection<GameData> getAll() {
        return this.gameList.values();
    }

    public void deleteAllGames() throws DataAccessException {
        this.gameList = new HashMap<>();
    }


}
