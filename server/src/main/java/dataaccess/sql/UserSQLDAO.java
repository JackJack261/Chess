package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import models.GameData;
import models.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSQLDAO {

    // createUser

    public void createUser(UserData userData) throws DataAccessException {
        String sql = "INSERT INTO UserData (username, passwordHash, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userData.username());
            ps.setString(2, userData.password());
            ps.setString(3, userData.email());

            ps.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to create user: " + e.getMessage(), e);
        }
    }

    // getUser
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, passwordHash, email FROM UserData WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String retrievedUsername = rs.getString("username");
                    String passwordHash = rs.getString("passwordHash");
                    String email = rs.getString("email");

                    return new UserData(retrievedUsername, passwordHash, email);
                }
                return null;
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to get user: " + e.getMessage(), e);
        }
    }


    // removeAll

    public void removeAll() throws DataAccessException {
        String sql = "TRUNCATE TABLE UserData";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (DataAccessException | SQLException e){
            throw new DataAccessException("Unable to delete user database: " + e.getMessage(), e);
        }
    }

}
