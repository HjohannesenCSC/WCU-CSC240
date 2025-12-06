package com.wcupa.csc240.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wcupa.csc240.model.Movie;
import com.wcupa.csc240.model.NewsMention;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DataApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    private static final Type MOVIE_LIST_TYPE =
            new TypeToken<List<Movie>>() {}.getType();
    private static final Type NEWS_LIST_TYPE =
            new TypeToken<List<NewsMention>>() {}.getType();

    public DataApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    // ---- Movies ----
    public List<Movie> getAllMovies() throws IOException, InterruptedException {
        String json = get("/movies");
        if (json == null || json.isBlank()) return Collections.emptyList();

        JsonElement root = JsonParser.parseString(json);

        if (root.isJsonArray()) {
            // Plain array: [ {..movie..}, {..movie..} ]
            return gson.fromJson(root, MOVIE_LIST_TYPE);
        } else if (root.isJsonObject()) {
            // Wrapped object: { "movies": [ ... ] } or { "results": [ ... ] }
            JsonObject obj = root.getAsJsonObject();
            if (obj.has("movies")) {
                return gson.fromJson(obj.get("movies"), MOVIE_LIST_TYPE);
            } else if (obj.has("results")) {
                return gson.fromJson(obj.get("results"), MOVIE_LIST_TYPE);
            }
        }

        return Collections.emptyList();
    }

    public Optional<Movie> getMovieById(int id) throws IOException, InterruptedException {
        String json = get("/movies/" + id);
        if (json == null || json.isBlank()) return Optional.empty();

        JsonElement root = JsonParser.parseString(json);

        if (root.isJsonObject()) {
            Movie m = gson.fromJson(root, Movie.class);
            return Optional.ofNullable(m);
        }

        return Optional.empty();
    }

    // ---- News ----
    public List<NewsMention> getAllNews() throws IOException, InterruptedException {
        String json = get("/news");
        if (json == null || json.isBlank()) return Collections.emptyList();

        JsonElement root = JsonParser.parseString(json);

        if (root.isJsonArray()) {
            // Plain array
            return gson.fromJson(root, NEWS_LIST_TYPE);
        } else if (root.isJsonObject()) {
            // Wrapped object: { "news": [ ... ] } or { "items": [ ... ] }
            JsonObject obj = root.getAsJsonObject();
            if (obj.has("news")) {
                return gson.fromJson(obj.get("news"), NEWS_LIST_TYPE);
            } else if (obj.has("items")) {
                return gson.fromJson(obj.get("items"), NEWS_LIST_TYPE);
            }
        }

        return Collections.emptyList();
    }

    // ---- helper ----
    private String get(String path) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        int code = resp.statusCode();
        if (code >= 200 && code < 300) {
            return resp.body();
        }
        // basic logging for debugging
        System.err.println("DataApiClient GET " + path + " -> HTTP " + code + ", body: " + resp.body());
        return "";
    }
}
