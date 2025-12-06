package com.wcupa.csc240.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wcupa.csc240.api.DataApiClient;
import com.wcupa.csc240.model.Movie;
import com.wcupa.csc240.model.NewsMention;
import com.wcupa.csc240.model.MovieSummary;

import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;



public class ClassApi {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void main(String[] args) {
        // Config
        //int port = Integer.parseInt(Optional.ofNullable(System.getenv("CLASS_API_PORT")).orElse("8082"));
        String dataApiBase = Optional.ofNullable(System.getenv("DATA_API_BASE"))
                                     .orElse("http://localhost:4567");
        //port(port);
        port(8082); //This is just to see if the port above is making it too complicated.
        DataApiClient client = new DataApiClient(dataApiBase);

        // Basic CORS (dev-friendly)
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            res.type("application/json");
        });
        options("/*", (req, res) -> "OK");

        // Health
        get("/health", (req, res) -> GSON.toJson(Map.of("status", "Class API Tier 2 is working!")));

        // GET /movie-summary/:id -> combine /movies/{id} + (/news filtered by movie_id)
        get("/movie-summary/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                Optional<Movie> mOpt = client.getMovieById(id);
                if (mOpt.isEmpty()) {
                    res.status(404);
                    return GSON.toJson(Map.of("error", "Movie " + id + " not found"));
                }

                // There is no /news by movie_id in Tier 1, so fetch /news and filter
                List<NewsMention> allNews = client.getAllNews();
                NewsMention nm = allNews.stream()
                        .filter(n -> n.getMovie_id() == id)
                        .findFirst().orElse(null);

                MovieSummary summary = MovieSummary.from(mOpt.get(), nm);
                return GSON.toJson(summary);

            } catch (Exception e) {
                res.status(502);
                return GSON.toJson(Map.of("error", "Failed to reach Data API", "details", e.getMessage()));
            }
        });

        // GET /movies/with-news?minMentions=10&limit=20
        get("/movies/with-news", (req, res) -> {
            try {
                int minMentions = parseIntOrDefault(req.queryParams("minMentions"), 0);
                int limit = Math.max(1, parseIntOrDefault(req.queryParams("limit"), 20));

                List<Movie> movies = client.getAllMovies();
                Map<Integer, NewsMention> newsByMovieId = client.getAllNews()
                        .stream()
                        .collect(Collectors.toMap(NewsMention::getMovie_id, n -> n, (a, b) -> a));

                List<MovieSummary> summaries = movies.stream()
                        .map(m -> MovieSummary.from(m, newsByMovieId.get(m.getId())))
                        .filter(s -> s.getMentionCount() >= minMentions)
                        .sorted(Comparator.comparingInt(MovieSummary::getMentionCount).reversed()
                                .thenComparingDouble(MovieSummary::getPopularity).reversed())
                        .limit(limit)
                        .collect(Collectors.toList());

                return GSON.toJson(summaries);

            } catch (Exception e) {
                res.status(502);
                return GSON.toJson(Map.of("error", "Failed to reach Data API", "details", e.getMessage()));
            }
        });

        // GET /highlights/top-mentioned?limit=5
        get("/highlights/top-mentioned", (req, res) -> {
            try {
                int limit = Math.max(1, parseIntOrDefault(req.queryParams("limit"), 5));
                List<Movie> movies = client.getAllMovies();
                Map<Integer, NewsMention> newsByMovieId = client.getAllNews()
                        .stream()
                        .collect(Collectors.toMap(NewsMention::getMovie_id, n -> n, (a, b) -> a));

                List<MovieSummary> top = movies.stream()
                        .map(m -> MovieSummary.from(m, newsByMovieId.get(m.getId())))
                        .sorted(Comparator.comparingInt(MovieSummary::getMentionCount).reversed()
                                .thenComparingDouble(MovieSummary::getPopularity).reversed())
                        .limit(limit)
                        .collect(Collectors.toList());

                return GSON.toJson(top);

            } catch (Exception e) {
                res.status(502);
                return GSON.toJson(Map.of("error", "Failed to reach Data API", "details", e.getMessage()));
            }
        });

        // Root help
        get("/", (req, res) -> GSON.toJson(Map.of(
                "service", "Tier 2 Class API",
                "endpoints", List.of(
                        "GET /health",
                        "GET /movie-summary/:id",
                        "GET /movies/with-news?minMentions=10&limit=20",
                        "GET /highlights/top-mentioned?limit=5"
                ),
                "dataApiBase", dataApiBase
        )));
    }

    private static int parseIntOrDefault(String s, int def) {
        try { return (s == null) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }
}
