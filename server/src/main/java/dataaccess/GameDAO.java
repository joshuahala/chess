package dataaccess;
import model.GameData;
public interface GameDAO {
    void createGame(String gameName, GameData gameData) throws DataAccessException;
}
