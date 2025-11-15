package client; // Or your main client package

// You will need to import your DTOs (Data Transfer Objects) from the shared module
// e.g., import chess.service.response.LoginResponse;
// e.g., import chess.service.request.LoginRequest;

//import server.src.main.java.*;


import com.google.gson.Gson; // For JSON serialization
import dataaccess.DataAccessException;
import models.*;
import requestsandresults.LoginRequest;
import requestsandresults.LogoutRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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

    // Create Games

    // Join Games


    public void clear() throws DataAccessException {
        this.makeRequest("DELETE", "/db", null, null, null);
    }
}