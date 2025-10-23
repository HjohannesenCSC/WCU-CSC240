import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DataApi {
    private static final String DB_URL = "jdbc:sqlite:../project.db";
    
    public static void main(String[] args) throws IOException {
    // Load SQLite JDBC driver
    try {
        Class.forName("org.sqlite.JDBC");
        System.out.println("SQLite JDBC driver loaded successfully");
    } catch (ClassNotFoundException e) {
        System.out.println("SQLite JDBC driver not found: " + e.getMessage());
        return;
    }
        // Test database connection first
        testDatabaseConnection();
        
        // Create HTTP server on port 4567
        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);
        
        // Health endpoint
        server.createContext("/health", new HealthHandler());
        
        // Movies endpoints - WITH ACTUAL DATABASE ACCESS
        server.createContext("/movies", new MoviesHandler());
        server.createContext("/movies/", new MovieByIdHandler());
        
        // News endpoints - WITH ACTUAL DATABASE ACCESS
        server.createContext("/news", new NewsHandler());
        server.createContext("/news/", new NewsByIdHandler());
        
        server.start();
        System.out.println("Data API running on http://localhost:4567");
        System.out.println("Connected to database: project.db");
        System.out.println("Test with: curl http://localhost:4567/health");
        System.out.println("Test real data: curl http://localhost:4567/movies");
    }
    
    private static void testDatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
    
    static class HealthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\": \"Data API Tier 1 is working with database!\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class MoviesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                List<Map<String, Object>> movies = new ArrayList<>();
                String sql = "SELECT * FROM movies";
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        Map<String, Object> movie = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            movie.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        movies.add(movie);
                    }
                }
                
                String response = convertToJson(movies);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
            } catch (SQLException e) {
                String error = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.length());
                exchange.getResponseBody().write(error.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
    
    static class MovieByIdHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            String id = parts[parts.length - 1];
            
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                String sql = "SELECT * FROM movies WHERE id = ?";
                Map<String, Object> movie = new HashMap<>();
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(id));
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        
                        for (int i = 1; i <= columnCount; i++) {
                            movie.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                    }
                }
                
                String response = convertToJson(movie);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
            } catch (SQLException e) {
                String error = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.length());
                exchange.getResponseBody().write(error.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
    
    static class NewsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                List<Map<String, Object>> newsList = new ArrayList<>();
                String sql = "SELECT * FROM news_mentions";
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        Map<String, Object> news = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            news.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        newsList.add(news);
                    }
                }
                
                String response = convertToJson(newsList);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
            } catch (SQLException e) {
                String error = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.length());
                exchange.getResponseBody().write(error.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
    
    static class NewsByIdHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            String id = parts[parts.length - 1];
            
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                String sql = "SELECT * FROM news_mentions WHERE id = ?";
                Map<String, Object> news = new HashMap<>();
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(id));
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        
                        for (int i = 1; i <= columnCount; i++) {
                            news.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                    }
                }
                
                String response = convertToJson(news);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
            } catch (SQLException e) {
                String error = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.length());
                exchange.getResponseBody().write(error.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
    
    // Simple JSON conversion method
    private static String convertToJson(Object data) {
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Map) {
                    sb.append(mapToJson((Map<?, ?>) list.get(i)));
                }
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        } else if (data instanceof Map) {
            return mapToJson((Map<?, ?>) data);
        }
        return "{}";
    }
    
    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (i++ > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}