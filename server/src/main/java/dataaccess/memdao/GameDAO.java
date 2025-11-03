package dataaccess.memdao;

import dataaccess.IGameDAO;
import models.GameData;

import java.util.HashMap;

public class GameDAO implements IGameDAO {

    HashMap<String, GameData> gameDatabase = new HashMap<>();

    // Define Methods Here

    // createGame
    public void createGame(GameData gameData) {
        gameDatabase.put(gameData.gameName(), gameData);
    }

    // getGame
    public GameData getGame(String gameName) {
        return gameDatabase.get(gameName);
    }

    public GameData getGameByID(int gameID) {
        for (GameData gameData : gameDatabase.values()) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    // listGames
    public HashMap<String, GameData> listGames() {
        return gameDatabase;
    }

    // updateGame
    public void updateGame(String gameName, GameData updatedGame) {
        gameDatabase.replace(gameName, updatedGame);
    }

    public void removeAll() {
        gameDatabase.clear();
    }


}
