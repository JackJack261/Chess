package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import models.*;
import requestsandresults.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class ServerFacade {

    private final String baseUrl;

    public ServerFacade(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    private <T> T makeRequest(String method, String path, Object requestBody, Class<T> responseClass, String authToken) throws DataAccessException {
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Debug Help
//            System.out.println("DEBUG: url = " + url);

            if (authToken != null && !authToken.isEmpty()) {
                http.addRequestProperty("Authorization", authToken);
            }

            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (requestBody != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String jsonBody = new Gson().toJson(requestBody);
                try (OutputStream os = http.getOutputStream()) {
                    os.write(jsonBody.getBytes());
                }
            }

            http.connect();

            if (http.getResponseCode() >= 400) {
                throw new DataAccessException("HTTP Error: " + http.getResponseCode() + " " + http.getResponseMessage());
            }

            return readResponseBody(http, responseClass);

        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static <T> T readResponseBody(HttpURLConnection http, Class<T> responseClass) throws Exception {
        T response = null;

        if (responseClass != null) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                response = new Gson().fromJson(reader, responseClass);
            }
        }

        return response;
    }

    public AuthData register(String username, String password, String email) throws DataAccessException {

        var path = "/user";

        UserData request = new UserData(username, password, email);
        // Debug print
        System.out.println("DEBUG: Request = " + request);

        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws DataAccessException {

        var path = "/session";

        var request = new LoginRequest(username, password);

        return this.makeRequest("POST", path, request, AuthData.class, null);

    }

    // Logout

    public void logout(String authToken) throws DataAccessException {
        var path = "/session";

        this.makeRequest("DELETE", path, null, null, authToken);
    }

    // List Games

    public List<GameInfo> listGames(String authToken) throws DataAccessException {
        var path = "/game";

        var response = this.makeRequest("GET", path, null, ListResult.class, authToken);

        return response.games();
    }

    // Create Games

    public int createGame(String authToken, String gameName) throws DataAccessException {
        var path = "/game";

        var request = new CreateRequest(authToken, gameName);

        var response = this.makeRequest("POST", path, request, CreateResult.class, authToken);

        return response.gameID();
    }

    // Join Games

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        var path = "/game";

        var request = new JoinRequest(authToken, playerColor, gameID);

        this.makeRequest("PUT", path, request, null, authToken);
    }


    public void clear() throws DataAccessException {
        this.makeRequest("DELETE", "/db", null, null, null);
    }
}