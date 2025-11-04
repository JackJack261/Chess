package dataaccess;

import dataaccess.sql.*;
import models.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DAOTests {

    private static AuthSQLDAO authDAO;
    private static UserSQLDAO userDAO;
    private static GameSQLDAO gameDAO;


    @BeforeAll
    public static void databaseCreation() throws DataAccessException {
        DatabaseManager.clearDatabase();
        DatabaseManager.configureDatabase();
        authDAO = new AuthSQLDAO();
        userDAO = new UserSQLDAO();
        gameDAO = new GameSQLDAO();
    }


    @BeforeEach
    public void setup() throws Exception {
        userDAO.removeAll();
        authDAO.removeAll();
        gameDAO.removeAll();
    }

    // createUser
    @Test
    public void registerTestPos() {
        Assertions.assertDoesNotThrow(()->{
            UserData userData = new UserData("Test1", "Test", "test@test.com");
            userDAO.createUser(userData);
        });
    }

    @Test
    public void registerTestNeg() {
        Assertions.assertThrows(DataAccessException.class, ()->{
            UserData userData = new UserData(null, null, null);
            userDAO.createUser(userData);
        });
    }

    // getUser

    @Test
    public void getUserPos() {
        Assertions.assertDoesNotThrow(()->{
            UserData userData = new UserData("test", "test", "test@test.com");
            userDAO.createUser(userData);

            userDAO.getUser("test");
        });
    }

    @Test
    public void getUserNeg() {
        Assertions.assertThrows(DataAccessException.class, ()->{
            userDAO.getUser("asdkfjlaskj");
        });
    }

    // removeAll

    @Test
    public void registerClear() {
        Assertions.assertDoesNotThrow(()->{
            userDAO.removeAll();
        });
    }

    // AUTH DAO

    // createAuth

    // getAuth

    // deleteAuth

    // removeAll

    // GAME DAO

    // createGame

    // getGame

    // getGameByID

    // listGames

    // updateGame

    // removeAll





}
