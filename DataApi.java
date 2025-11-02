import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Optional;

public class DataApi {
    public static void main(String[] args) throws IOException {
    // Create HTTP server on port (use DATA_API_PORT env var or default 4567)
    int port = Integer.parseInt(Optional.ofNullable(System.getenv("DATA_API_PORT")).orElse("4567"));
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Health endpoint
        server.createContext("/health", new HealthHandler());
        
        // Movies endpoints
        server.createContext("/movies", new MoviesHandler());
        server.createContext("/movies/", new MovieByIdHandler());
        
        // News endpoints
        server.createContext("/news", new NewsHandler());
        server.createContext("/news/", new NewsByIdHandler());
        
        server.start();
    System.out.println("✅ Data API running on http://localhost:" + port);
    System.out.println("📝 Test with: curl http://localhost:" + port + "/health");
    }
    
    static class HealthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\": \"Data API Tier 1 is working!\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class MoviesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\": \"Movies endpoint - would return all movies\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class MovieByIdHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\": \"Movie by ID endpoint\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class NewsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\": \"News endpoint - would return all news\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class NewsByIdHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\": \"News by ID endpoint\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}