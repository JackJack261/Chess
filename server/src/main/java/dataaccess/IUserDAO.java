package dataaccess;

import models.UserData;

public interface IUserDAO {

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void removeAll() throws DataAccessException;

}
