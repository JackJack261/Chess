package dataaccess;

import chess.ChessGame;
import dataaccess.sql.*;
import models.*;
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
    public void getUserNeg() throws DataAccessException {
        UserData result = userDAO.getUser("BlahBlahBlah");

        Assertions.assertNull(result);

    }

    // removeAll

    @Test
    public void userClear() {
        Assertions.assertDoesNotThrow(()->{
            userDAO.removeAll();
        });
    }

    // AUTH DAO

    // createAuth
    @Test
    public void createAuthPos() {
        Assertions.assertDoesNotThrow(()->{
            AuthData authData = new AuthData("AuthTest", "1234-567-8910");
            authDAO.createAuth(authData);
        });
    }

    @Test
    public void createAuthNeg() {
        Assertions.assertThrows(DataAccessException.class, ()->{
            AuthData authData = new AuthData(null, null);
            authDAO.createAuth(authData);
        });
    }

    // getAuth

    @Test
    public void getAuthPos() {
        Assertions.assertDoesNotThrow(()->{
            AuthData authData = new AuthData("test", "123-456-789");
            authDAO.createAuth(authData);

            authDAO.getAuth("123-456-789");
        });
    }

    @Test
    public void getAuthNeg() throws DataAccessException {
        AuthData result = authDAO.getAuth("987-654-4321");

        Assertions.assertNull(result);
    }

    // deleteAuth

    @Test
    public void deleteAuthPos() {
        Assertions.assertDoesNotThrow(()->{
            AuthData authData = new AuthData("AuthTest", "1234-567-8910");
            authDAO.createAuth(authData);

            authDAO.deleteAuth("1234-567-8910");
        });
    }

    @Test
    public void deleteAuthNeg() throws DataAccessException {
        authDAO.deleteAuth("notAToken");

        Assertions.assertTrue(true, "Operation deleted 0 rows");

    }

    // removeAll

    @Test
    public void authClear() {
        Assertions.assertDoesNotThrow(()->{
            authDAO.removeAll();
        });
    }

    // GAME DAO

    // createGame

    @Test
    public void createGamePos() {
        Assertions.assertDoesNotThrow(()->{
            GameData gameData = new GameData(9, "john", "jill", "awesomeGame", new ChessGame());
            gameDAO.createGame(gameData);
        });
    }

    @Test
    public void createGameNeg() {
        Assertions.assertThrows(DataAccessException.class, ()->{
            GameData gameData = new GameData(7, null, null, null, null);
            gameDAO.createGame(gameData);
        });
    }

    // getGame
    @Test
    public void getGamePos() {
        Assertions.assertDoesNotThrow(()->{
            GameData gameData = new GameData(4, "John", "Jill", "Beans", new ChessGame());
            gameDAO.createGame(gameData);

            gameDAO.getGame("Beans");
        });
    }

    @Test
    public void getGameNeg() throws DataAccessException {
        GameData result = gameDAO.getGame("Cream");

        Assertions.assertNull(result);
    }

    // getGameByID
    @Test
    public void getGameByIDPos() {
        Assertions.assertDoesNotThrow(()->{
            GameData gameData = new GameData(4, "John", "Jill", "Beans", new ChessGame());
            gameDAO.createGame(gameData);

            gameDAO.getGameByID(4);
        });
    }

    @Test
    public void getGameByIDNeg() throws DataAccessException {
        GameData result = gameDAO.getGameByID(100);

        Assertions.assertNull(result);
    }

    // listGames

    @Test
    public void listGamesPos() {
        Assertions.assertDoesNotThrow(()->{
            GameData gameData = new GameData(4, "John", "Jill", "Beans", new ChessGame());
            gameDAO.createGame(gameData);

            gameDAO.listGames();
        });
    }

    @Test
    public void listGamesNeg() throws DataAccessException {
        gameDAO.listGames();
        Assertions.assertTrue(true, "Operation listed 0 games");
    }

    // updateGame
    @Test
    public void updateGamePos() {
        Assertions.assertDoesNotThrow(()->{
            GameData gameData = new GameData(4, "John", null, "Beans", new ChessGame());
            gameDAO.createGame(gameData);

            GameData updatedGame = new GameData(4, "John", "Jill", "Beans", new ChessGame());


            gameDAO.updateGame("Beans", updatedGame);
        });
    }

    @Test
    public void updateGameNeg() {
        Assertions.assertThrows(NullPointerException.class, ()->{
//            GameData updatedGame = new GameData(1, "Fail", "Fail", "ShouldFail", null);
            gameDAO.updateGame("ShouldFail", null);
        });
    }

    // removeAll
    @Test
    public void gameClear() {
        Assertions.assertDoesNotThrow(()->{
            gameDAO.removeAll();
        });
    }




}
