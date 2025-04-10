package service;

import chess.*;
import dataaccess.*;
import model.*;
import websocket.commands.MakeMoveCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameService {

    public GameDAO gameDAO;
    public AuthDAO authDAO;
    public UserDAO userDAO;
    private int latestID = 1234;
    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        //check for null
        if (createGameRequest.gameName() == null) {
            throw new DataAccessException(400, "bad request");
        }
        if (createGameRequest.gameName().equals("")) {
            throw new DataAccessException(400, "bad request");
        }

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

    public ListGamesResult getAll(String authToken) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException(401, "unauthorized");
        }
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
        // check for null values
        if (joinRequest.playerColor() == null || joinRequest.gameID() == null) {
            throw new DataAccessException(400, "bad request: please enter a valid value");
        }
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
            if (gameData.whiteUsername() != null) {
                throw new DataAccessException(403, "Forbidden");
            }
            GameData modifiedGameData = new GameData(gameData.gameID(), userData.username(), gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
            gameDAO.updateGame(modifiedGameData);
        } else if (Objects.equals(joinRequest.playerColor().toLowerCase(), "black")) {
            if (gameData.blackUsername() != null) {
                throw new DataAccessException(403, "Forbidden");
            }
            GameData modifiedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), userData.username(),
                    gameData.gameName(), gameData.game());
            gameDAO.updateGame(modifiedGameData);
        } else {
            throw new DataAccessException(400, "bad request. please enter a valid color, eg. black or white.");
        }
    }

    public void leave(String authToken, int gameID) throws DataAccessException {
        // check authorization
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException(401, "unauthorized");
        }
        // get userData
        UserData userData = userDAO.getUser(authData.username());
        try {
            GameData game = gameDAO.getGame(gameID);
            var updatedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
            if (Objects.equals(game.whiteUsername(), authData.username())) {
                updatedGame = new GameData(game.gameID(), null, game.blackUsername(),game.gameName(), game.game());
            } else {
                updatedGame = new GameData(game.gameID(), game.whiteUsername(), null ,game.gameName(), game.game());
            }
            gameDAO.updateGame(updatedGame);
        } catch (Exception ex) {
            authData = null;
        }
    }

    public void deleteAllGames() throws DataAccessException {
        gameDAO.deleteAll();
    }
    public GameData makeMove(MakeMoveCommand moveCommand) throws DataAccessException {
        ChessGame.TeamColor teamColor;
        if (Objects.equals(moveCommand.team, "white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        ChessMove move = moveCommand.move;
        // get game
        GameData gameData = gameDAO.getGame(moveCommand.getGameID());
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        // validate move
        MovesCalculator movesCalculator = new MovesCalculator(game.getBoard(),move.getStartPosition());
        ArrayList<ChessMove> possibleMoves = movesCalculator.possibleMoves(piece.getPieceType(), teamColor);



        // update game in db
        try {
        // send back updated game to client
            game.makeMove(move);
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updatedGame);
            return updatedGame;
        } catch (Exception ex) {
            System.out.println("Error updating game move");
        }
        return gameData;
    }

    private int newID() {
        latestID ++;
        return latestID;
    }
}
