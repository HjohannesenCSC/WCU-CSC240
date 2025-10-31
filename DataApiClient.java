package com.yourteam.classapi.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourteam.classapi.model.Movie;
import com.yourteam.classapi.model.NewsMention;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DataApiClient {

    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final Gson gson = new Gson();

    private static final Type MOVIE_LIST_TYPE =
            new TypeToken<List<Movie>>() {}.getType();
    private static final Type NEWS_LIST_TYPE =
            new TypeToken<List<NewsMention>>() {}.getType();

    public DataApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.httpClient = HttpClients.createDefault();
    }

    public List<Movie> getAllMovies() throws IOException {
        String json = get("/movies");
        return gson.fromJson(json, MOVIE_LIST_TYPE);
    }

    public Optional<Movie> getMovieById(int id) throws IOException {
        String json = get("/movies/" + id);
        Movie m = gson.fromJson(json, Movie.class);
        return Optional.ofNullable(m);
    }

    public List<NewsMention> getAllNews() throws IOException {
        String json = get("/news");
        return gson.fromJson(json, NEWS_LIST_TYPE);
    }

    // --- helper ---
    private String get(String path) throws IOException {
        String url = baseUrl + path;
        HttpGet req = new HttpGet(url);
        try (ClassicHttpResponse resp = httpClient.executeOpen(null, req, null)) {
            int code = resp.getCode();
            if (code >= 200 && code < 300) {
                return EntityUtils.toString(resp.getEntity());
            }
            return "[]";
        }
    }
}
