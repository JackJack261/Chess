package dataaccess;

import models.UserData;

import java.util.HashMap;

public class UserDAO {


    HashMap<String, UserData> userDatabase = new HashMap<>();

    public void createUser(UserData userData) {
        userDatabase.put(userData.username(), userData);
    }

    public UserData getUser(String username) {
        return userDatabase.get(username);
    }

}