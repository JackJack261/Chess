package dataaccess;

import models.GameData;

import java.util.HashMap;

 public interface IGameDAO {
    // createGame
     void createGame(GameData gameData) throws DataAccessException;

    // getGame
     GameData getGame(String gameName) throws DataAccessException;

     GameData getGameByID(int gameID) throws DataAccessException;

    // listGames
     HashMap<String, GameData> listGames() throws DataAccessException;

    // updateGame
     void updateGame(String gameName, GameData updatedGame) throws DataAccessException;

     void removeAll() throws DataAccessException;
}
