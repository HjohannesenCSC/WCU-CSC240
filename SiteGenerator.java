import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SiteGenerator {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Phase 3 Part 1 Site Generator ===");
        System.out.println("WCU-CSC 240 - Team: Rylee, Hooria, Henry, Matt");
        
        String url = "http://localhost:8083/dashboard";
        System.out.println("Calling UI API endpoint: " + url);
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            System.err.println("Error: HTTP " + response.statusCode());
            return;
        }
        
        String json = response.body();
        System.out.println("Received JSON data from UI API");
        
        Files.createDirectories(Paths.get("target/site"));
        
        String html = "<html><body><h1>Phase 3 Part 1 - Site Generator</h1>" +
                     "<p>WCU-CSC 240 - Team: Hooria, Rylee, Henry, Matt</p>" +
                     "<h2>Dashboard Data from UI API:</h2>" +
                     "<pre>" + json + "</pre>" +
                     "</body></html>";
        
        Files.writeString(Paths.get("target/site/index.html"), html);
        
        System.out.println("Generated: target/site/index.html");
        System.out.println("=== Part 1 Complete ===");
    }
}