package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlGameDAO implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void createGame(int gameID, GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, game, gameOver)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        var gameJson = new Gson().toJson(gameData.game());
        executeUpdate(statement, gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameJson, gameData.gameOver());

    }

    @Override
    public void updateGame(GameData newGame) throws DataAccessException {
        if (getGame(newGame.gameID()) == null) {
            throw new DataAccessException(400, "Bad Request");
        }
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, game=?, gameOver=? WHERE id=?";
        var jsonGame = new Gson().toJson(newGame.game());
        var id = executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), jsonGame, newGame.gameOver(), newGame.gameID());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game, gameOver FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    } else {
                        throw new DataAccessException(400, "Bad Request");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public Collection<GameData> getAll() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            Collection<GameData> games = new ArrayList<>();
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game, gameOver FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var game = readGameData(rs);
                        games.add(game);

                    }
                    return games;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        executeUpdate(statement);
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("game");
        var gameOver = rs.getString("gameOver");  // or use rs.getBoolean("gameOver")

        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game, gameOver);
    }
//
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return rs.getInt(1);
                    }
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
    //
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              `gameOver` varchar(256),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            )
            """
    };
    //
//
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}