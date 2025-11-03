package dataaccess;

import models.AuthData;

public interface IAuthDAO {

    // createAuth
    void createAuth(AuthData authData) throws DataAccessException;
    // getAuth
    AuthData getAuth(String authToken) throws DataAccessException;

    // deleteAuth
    void deleteAuth(String authToken) throws DataAccessException;
    void removeAll() throws DataAccessException;


}
