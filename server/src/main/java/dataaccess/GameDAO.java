package dataaccess;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    void createGame(int gameID, GameData gameData) throws DataAccessException;
    Collection<GameData> getAll() throws DataAccessException;

    void updateGame(GameData newGame) throws DataAccessException;
}
