package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import models.AuthData;
import models.UserData;

import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthSQLDAO {

    // createAuth

    public void createAuth(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO AuthData (username, authToken) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authData.username());
            ps.setString(2, authData.authToken());

            ps.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to create authentication: " + e.getMessage(), e);
        }
    }


    // getAuth

    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        String sql = "SELECT username, authToken FROM AuthData WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String retrievedUsername = rs.getString("username");
                    String retrievedauthToken = rs.getString("authToken");

                    return new AuthData(retrievedUsername, retrievedauthToken);
                }
                return null;

            } catch (SQLException e) {
                throw new DataAccessException("Unable to get auth: " + e.getMessage(), e);
            }
        }
    }

    // removeAuth
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthData WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authToken);

            ps.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Unable to delete auth: " + e.getMessage(), e);
        }
    }

    // removeAll

    public void removeAll() throws DataAccessException {
        String sql = "TRUNCATE TABLE AuthData";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (DataAccessException | SQLException e){
            throw new DataAccessException("Unable to delete user database: " + e.getMessage(), e);
        }
    }



}


