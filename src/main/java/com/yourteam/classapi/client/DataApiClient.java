package com.yourteam.classapi.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourteam.classapi.model.Movie;
import com.yourteam.classapi.model.NewsMention;

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
        return gson.fromJson(json, MOVIE_LIST_TYPE);
    }

    public Optional<Movie> getMovieById(int id) throws IOException, InterruptedException {
        String json = get("/movies/" + id);
        if (json == null || json.isBlank()) return Optional.empty();
        Movie m = gson.fromJson(json, Movie.class);
        return Optional.ofNullable(m);
    }

    // ---- News ----
    public List<NewsMention> getAllNews() throws IOException, InterruptedException {
        String json = get("/news");
        if (json == null || json.isBlank()) return Collections.emptyList();
        return gson.fromJson(json, NEWS_LIST_TYPE);
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
        // you can log here
        return "";
    }
}
