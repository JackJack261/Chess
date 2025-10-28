package dataaccess;

import models.AuthData;

import java.util.HashMap;

public class AuthDAO {

    HashMap<String, AuthData> authDatabase = new HashMap<>();

    // Define methods here

    // createAuth
    public void createAuth(AuthData authData) {
        authDatabase.put(authData.authToken(), authData);
    }
    // getAuth
    public AuthData getAuth(String authToken) {
        return authDatabase.get(authToken);
    }

    // deleteAuth
    public void deleteAuth(String authToken) {
        authDatabase.remove(authToken);
    }

    public void removeAll() {
        authDatabase.clear();
    }
}
