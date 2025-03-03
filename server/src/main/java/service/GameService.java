package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

public class GameService {

    public MemoryGameDAO gameDAO = new MemoryGameDAO();
    public MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private int latestID = 1234;
    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        // check authentication
        String authToken = createGameRequest.authToken();
        // get authData
        // if authData is null, throw error
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException(401, "unauthorized");
        }
        // create gameData
        ChessGame game = new ChessGame();
        int newID = newID();
        String gameName = createGameRequest.gameName();
        GameData gameData = new GameData(newID, null, null, gameName, game);
        // add gameData to "database"
        gameDAO.createGame(gameName, gameData);
        // return result
        CreateGameResult createGameResult = new CreateGameResult(newID);
        return createGameResult;
    }

    private int newID() {
        latestID ++;
        return latestID;
    }
}
