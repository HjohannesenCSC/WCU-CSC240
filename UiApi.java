package com.yourteam.classapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static spark.Spark.*;



public class UiApi {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String CLASS_API_BASE = "http://localhost:8082";

    public static void main(String[] args) {
        // Use port 8083 for UI API
        port(8083);

        // Enable CORS for frontend development
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            res.type("application/json");
        });
        options("/*", (req, res) -> "OK");

        // 1. Health Check Endpoint
        get("/health", (req, res) -> {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UI API Tier 3 is healthy");
            response.put("port", 8083);
            response.put("service", "UI API");
            return GSON.toJson(response);
        });

        // 2. Dashboard Endpoint - Main UI component
        get("/dashboard", (req, res) -> {
            try {
                Map<String, Object> dashboard = new HashMap<>();
                
                // Aggregate data from multiple Class API endpoints
                String topMovies = callClassApi("/highlights/top-mentioned?limit=5");
                String moviesWithNews = callClassApi("/movies/with-news?limit=8");
                
                // Format for frontend
                dashboard.put("featuredMovies", GSON.fromJson(topMovies, Object.class));
                dashboard.put("recentMovies", GSON.fromJson(moviesWithNews, Object.class));
                dashboard.put("lastUpdated", new Date().toString());
                dashboard.put("uiOptimized", true);
                
                return GSON.toJson(dashboard);
                
            } catch (Exception e) {
                res.status(502);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Unable to load dashboard data");
                error.put("details", e.getMessage());
                return GSON.toJson(error);
            }
        });

        // 3. Movie Details Endpoint - Formatted for UI
        get("/movie/:id", (req, res) -> {
            try {
                String movieId = req.params(":id");
                String movieSummary = callClassApi("/movie-summary/" + movieId);
                
                Map<String, Object> uiResponse = new HashMap<>();
                uiResponse.put("movieDetails", GSON.fromJson(movieSummary, Object.class));
                uiResponse.put("formattedFor", "user_interface");
                uiResponse.put("includesUIElements", Arrays.asList("rating_stars", "news_count", "popularity_badge"));
                
                return GSON.toJson(uiResponse);
                
            } catch (Exception e) {
                res.status(404);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Movie not found or unavailable");
                return GSON.toJson(error);
            }
        });

        // 4. Movies List Endpoint - UI optimized
        get("/movies", (req, res) -> {
            try {
                String limit = req.queryParams("limit") != null ? req.queryParams("limit") : "10";
                String moviesWithNews = callClassApi("/movies/with-news?limit=" + limit);
                
                Map<String, Object> uiResponse = new HashMap<>();
                uiResponse.put("movies", GSON.fromJson(moviesWithNews, Object.class));
                uiResponse.put("totalCount", GSON.fromJson(moviesWithNews, ArrayList.class).size());
                uiResponse.put("uiPagination", true);
                
                return GSON.toJson(uiResponse);
                
            } catch (Exception e) {
                res.status(502);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Unable to load movies list");
                return GSON.toJson(error);
            }
        });

        // 5. Status check for Class API connectivity
        get("/status", (req, res) -> {
            try {
                String classApiHealth = callClassApi("/health");
                Map<String, Object> status = new HashMap<>();
                status.put("uiApi", "running");
                status.put("classApi", "connected");
                status.put("classApiStatus", GSON.fromJson(classApiHealth, Object.class));
                return GSON.toJson(status);
                
            } catch (Exception e) {
                Map<String, Object> status = new HashMap<>();
                status.put("uiApi", "running");
                status.put("classApi", "disconnected");
                status.put("error", e.getMessage());
                return GSON.toJson(status);
            }
        });

        System.out.println("=== UI API (Tier 3) Started ===");
        System.out.println("Port: 8083");
        System.out.println("Purpose: Format data for frontend UI");
        System.out.println("Class API: " + CLASS_API_BASE);
        System.out.println("Endpoints:");
        System.out.println("  GET /health          - Service health check");
        System.out.println("  GET /dashboard       - Main dashboard data");
        System.out.println("  GET /movie/{id}      - Movie details for UI");
        System.out.println("  GET /movies          - Movies list for UI");
        System.out.println("  GET /status          - Connectivity status");
    }

    private static String callClassApi(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(CLASS_API_BASE + endpoint))
            .GET()
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Class API error: " + response.statusCode());
        }
        
        return response.body();
    }
}