package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.IGameDAO;
import models.GameData;

import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class GameSQLDAO implements IGameDAO {


    // createGame
    public void createGame(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?, ?)";

        String serializedGame = serialize(gameData.game());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameData.gameID());
            ps.setString(2, gameData.whiteUsername());
            ps.setString(3, gameData.blackUsername());
            ps.setString(4, gameData.gameName());
            ps.setString(5, serializedGame); // The full game state

            ps.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to create game: " + e.getMessage(), e);
        }
    }

    private String serialize(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserialize(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }

    // getGame
    public GameData getGame(String gameName) throws DataAccessException {
        String sql = "SELECT * FROM GameData WHERE gameName = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gameName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int retrievedGameID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String retrievedGameName = rs.getString("gameName");
                    String game = rs.getString("gameData");

                    ChessGame deserializedGame = deserialize(game);

                    return new GameData(retrievedGameID, whiteUsername, blackUsername, retrievedGameName, deserializedGame);

                }

                return null;
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to get game by game name: " + e.getMessage(), e);
        }
    }

    // getGameByID

    public GameData getGameByID(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM GameData WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int retrievedGameID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String retrievedGameName = rs.getString("gameName");
                    String game = rs.getString("gameData");

                    ChessGame deserializedGame = deserialize(game);

                    return new GameData(retrievedGameID, whiteUsername, blackUsername, retrievedGameName, deserializedGame);

                }

                return null;
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to get game by ID: " + e.getMessage(), e);
        }
    }

    // listGames
    public HashMap<String, GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM GameData";
        HashMap<String, GameData> games = new HashMap<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int gameId = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String game = rs.getString("gameData");

                ChessGame deserializedGame = deserialize(game);
                GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, deserializedGame);

                games.put(gameName, gameData);
            }

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to list all games: " + e.getMessage(), e);
        }
        return games;
    }

    // updateGame

    public void updateGame(String gameName, GameData updatedGame) throws DataAccessException {
        String sql = "UPDATE GameData SET whiteUsername = ?, blackUsername = ? WHERE gameName = ?";

        String updatedWhiteUsername = updatedGame.whiteUsername();
        String updatedBlackUsername = updatedGame.blackUsername();


        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, updatedWhiteUsername);
            ps.setString(2, updatedBlackUsername);
            ps.setString(3, gameName);

            ps.executeUpdate();


        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to update game: " + e.getMessage(), e);
        }
    }

    // removeAll

    public void removeAll() throws DataAccessException {
        String sql = "TRUNCATE TABLE GameData";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (DataAccessException | SQLException e){
            throw new DataAccessException("Unable to delete game database: " + e.getMessage(), e);
        }
    }


}
