package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameService {

    public MemoryGameDAO gameDAO = new MemoryGameDAO();
    public MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public MemoryUserDAO userDAO = new MemoryUserDAO();
    private int latestID = 1234;
    public GameService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
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
        gameDAO.createGame(newID, gameData);
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

    public void joinGame(JoinGameRequest joinRequest, String authToken) throws DataAccessException {
        // verifies that the game exists and adds the caller as the
        // requested color to the game
        // returns an object with playerColor and GameID

        // check authorization
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException(401, "unauthorized");
        }
        // get userData
        UserData userData = userDAO.getUser(authData.username());

        // verify game
        int gameID = Integer.parseInt(joinRequest.gameID());
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException(400, "No such game exists");
        }
        if (Objects.equals(joinRequest.playerColor().toLowerCase(), "white")) {
            GameData modifiedGameData = new GameData(gameData.gameID(), userData.username(), null,
                    gameData.gameName(), gameData.game());
            gameDAO.updateGame(modifiedGameData);
        } else if (Objects.equals(joinRequest.playerColor().toLowerCase(), "black")) {
            GameData modifiedGameData = new GameData(gameData.gameID(), null, userData.username(),
                    gameData.gameName(), gameData.game());
            gameDAO.updateGame(modifiedGameData);
        } else {
            throw new DataAccessException(400, "bad request. please enter a valid color, eg. black or white.");
        }



    }

    public void deleteAllGames() throws DataAccessException {
        gameDAO.deleteAllGames();
    }

    private int newID() {
        latestID ++;
        return latestID;
    }
}
