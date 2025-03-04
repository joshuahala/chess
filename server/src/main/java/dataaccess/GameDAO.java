package dataaccess;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    void createGame(String gameName, GameData gameData) throws DataAccessException;
    Collection<GameData> getAll() throws DataAccessException;
}
