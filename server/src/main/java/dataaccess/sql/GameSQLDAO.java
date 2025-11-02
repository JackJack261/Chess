package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import models.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GameSQLDAO {


    // createGame
    public void createGame(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?, ?)";

        String serializedGame = serialize(gameData.game()); // assuming gameData has a .game() method

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

    // getGame

    // getGameByID

    // listGames

    // updateGame

    // removeAll


}
