package dataaccess;
import model.GameData;
public interface GameDAO {
    void createGame(String gameName) throws DataAccessException;
    GameData getGame(String authToken) throws DataAccessException;
}
