package dataaccess;

import models.GameData;

import java.util.HashMap;

public class GameDAO {

    HashMap<String, GameData> gameDatabase = new HashMap<>();

    // Define Methods Here

    // createGame
    public void createGame(GameData gameData) {
        gameDatabase.put(String.valueOf(gameData.gameID()), gameData);
    }

    // getGame
    public GameData getGame(int gameID) {
        return gameDatabase.get(String.valueOf(gameID));
    }

    // listGames
    public HashMap<String, GameData> listGames() {
        return gameDatabase;
    }

    // updateGame
    public void updateGame(int gameID, GameData updatedGame) {
        gameDatabase.replace(String.valueOf(gameID), gameDatabase.get(String.valueOf(gameID)), updatedGame);
    }

    public void removeAll() {
        gameDatabase.clear();
    }


}
