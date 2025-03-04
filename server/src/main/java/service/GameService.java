package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.*;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public ListGamesResult getAll() throws DataAccessException {
        Collection<GameData> gamesList = gameDAO.getAll();
        Collection<GameDataWithoutGames> modifiedGamesList = gamesList.stream()
                .map(gameData -> new GameDataWithoutGames(gameData.gameID(), gameData.whiteUsername(),
                        gameData.blackUsername(), gameData.gameName()))
                .collect(Collectors.toList());

        return new ListGamesResult(modifiedGamesList);
    }

    private int newID() {
        latestID ++;
        return latestID;
    }
}
