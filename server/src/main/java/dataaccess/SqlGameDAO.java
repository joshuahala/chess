package dataaccess;

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
        var statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, game)" +
                " VALUES (?, ?, ?, ?, ?)";
        var gameJson = new Gson().toJson(gameData.game());
        executeUpdate(statement, gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameJson);

    }

    @Override
    public void updateGame(GameData newGame) throws DataAccessException {
        int num = 0;

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> getAll() throws DataAccessException {
        return null;
    }

    //
//    public Pet getPet(int id) throws ResponseException {
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet WHERE id=?";
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, id);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return readPet(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return null;
//    }
//
//    public Collection<Pet> listPets() throws ResponseException {
//        var result = new ArrayList<Pet>();
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet";
//            try (var ps = conn.prepareStatement(statement)) {
//                try (var rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        result.add(readPet(rs));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return result;
//    }
//
//    public void deletePet(Integer id) throws ResponseException {
//        var statement = "DELETE FROM pet WHERE id=?";
//        executeUpdate(statement, id);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var statement = "TRUNCATE pet";
//        executeUpdate(statement);
//    }
//
//    private Pet readPet(ResultSet rs) throws SQLException {
//        var id = rs.getInt("id");
//        var json = rs.getString("json");
//        var pet = new Gson().fromJson(json, Pet.class);
//        return pet.setId(id);
//    }
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
                    return rs.getInt(1);
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