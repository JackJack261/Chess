package dataaccess;

import models.GameData;

import java.util.HashMap;

public class GameDAO {

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
        return gameDatabase.get(gameID);
    }

    // listGames
    public HashMap<String, GameData> listGames() {
        return gameDatabase;
    }

    // updateGame
    public void updateGame(String gameName, GameData updatedGame) {
        gameDatabase.replace(gameName, gameDatabase.get(gameName), updatedGame);
    }

    public void removeAll() {
        gameDatabase.clear();
    }


}
