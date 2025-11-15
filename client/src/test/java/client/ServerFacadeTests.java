package client;

import models.GameData;
import org.junit.jupiter.api.*;
import requestsandresults.GameInfo;
import server.Server;

import org.junit.jupiter.api.*;
import dataaccess.DataAccessException;
import models.AuthData;

import javax.xml.crypto.Data;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Assertions;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl); //Fix this in ServerFacade
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTestSuccess() throws DataAccessException {

        var authData = facade.register("Test", "Test", "Test@test.com");

        assertNotNull(authData, "AuthData object should not be null");
        assertEquals("Test", authData.username(), "Username should match the request");
        assertNotNull(authData.authToken(), "AuthToken should not be null");
        assertTrue(authData.authToken().length() > 10, "AuthToken should be a non-empty string");
    }

    @Test
    public void registerFailure() throws DataAccessException {
        facade.register("player1", "password123", "p1@email.com");

        assertThrows(DataAccessException.class, () -> {
            facade.register("player1", "password123", "p1@email.com");
        }, "Registering a duplicate user should throw an exception");
    }


    @Test
    public void loginSuccess() throws DataAccessException {

        var authData = facade.register("Test", "Test", "Test@test.com");

        var loginData = facade.login("Test", "Test");

        assertNotNull(loginData, "LoginData should not be null");
        assertNotNull(loginData.authToken(), "AuthToken should not be null");
    }

    @Test
    public void loginFailure() throws DataAccessException {
        var authData = facade.register("Test", "Test", "Test@test.com");

        assertThrows(DataAccessException.class, () -> {
            facade.login("WrongUser", "No password");
        }, "Logging in with incorrect user should throw exception");

    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        var authData = facade.register("Test", "Test", "Test@test.com");

        String authToken = authData.authToken();

        assertDoesNotThrow(() -> facade.logout(authToken), "Logging out should not throw error");

    }

    @Test
    public void logoutFailure() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.logout("shouldBeWrong");
        }, "Should throw an error");
    }


    @Test
    public void createSuccess() throws DataAccessException {
        var authData = facade.register("Test", "Test", "Test@test.com");

        String authToken = authData.authToken();

        int gameID = assertDoesNotThrow(() -> {
            return facade.createGame(authToken, "TestGame");
        });

        assertTrue(gameID > 0, "The gameID should be positive integer");
    }

    @Test
    public void createFailure() throws DataAccessException {
        var authData = facade.register("Test", "Test", "Test@test.com");

        String authToken = authData.authToken();

        assertThrows(DataAccessException.class, () -> {
            facade.createGame(authToken, null);
        });
    }

    @Test
    public void listSuccess() throws DataAccessException {
        var authData = facade.register("Test", "Test", "Test@test.com");

        String authToken = authData.authToken();

        facade.createGame(authToken, "Test Game");

        List<GameInfo> listedGames = assertDoesNotThrow(() -> facade.listGames(authToken));

        assertNotNull(listedGames, "Listed games should not be null");

    }

    @Test
    public void listFailure() throws DataAccessException {
        String badAuthToken = "Bad Auth Token";

        assertThrows(DataAccessException.class, () -> {
            facade.listGames(badAuthToken);
        }, "Listing with bad auth token should throw error");
    }

}
