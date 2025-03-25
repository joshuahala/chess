package sharedserver;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private String port = "8080";
    private String serverUrl = "http://localhost:";

    public ServerFacade(int port) {
        this.port = Integer.toString(port);
        this.serverUrl = "http://localhost:" + this.port;
    }


    public RegisterResult register(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", null, path, userData, RegisterResult.class);
    }
    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", null, path, null, null);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", null,  path, loginRequest, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException {
        var path = "/session";
        return makeRequest("DELETE", logoutRequest.authToken(), path, logoutRequest, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", createGameRequest.authToken(), path, createGameRequest, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", authToken, path, null, ListGamesResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authtoken) throws ResponseException {
        var path = "/game";
        return makeRequest("PUT", authtoken, path, joinGameRequest, JoinGameResult.class);
    }

    private <T> T makeRequest(String method, String property, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (property != null) {
                http.addRequestProperty("Authorization", property);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}