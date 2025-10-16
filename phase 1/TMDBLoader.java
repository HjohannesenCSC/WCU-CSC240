import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TMDBLoader {

    private static final String TMDB_API_KEY = "827527332e093405432ffa9085c93b0a";
    private static final String DB_URL = "jdbc:sqlite:project.db";

    public static void main(String[] args) {
        System.out.println("Starting TMDb data load...");
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("Connected to the database.");

                // STEP 4: DELETE old data for a clean slate
                cleanupTable(conn);

                // Fetch data from TMDb API
                String jsonResponse = fetchDataFromTMDB();

                // Parse JSON and INSERT new data
                int recordsInserted = parseAndInsertData(conn, jsonResponse);

                // Write the summary file
                writeSummaryFile(recordsInserted);

                System.out.println("Data load finished successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void cleanupTable(Connection conn) throws Exception {
        String sql = "DELETE FROM movies;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Cleaned up existing data in 'movies' table.");
        }
    }

    private static String fetchDataFromTMDB() throws Exception {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Successfully fetched data from TMDb API.");
            return response.body();
        } else {
            throw new RuntimeException("API request failed with code: " + response.statusCode());
        }
    }

    private static int parseAndInsertData(Connection conn, String jsonResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode resultsNode = rootNode.get("results");

        String insertSQL = "INSERT INTO movies (id, title, popularity, release_date) VALUES (?, ?, ?, ?);";

        int count = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            for (JsonNode movieNode : resultsNode) {
                int id = movieNode.get("id").asInt();
                String title = movieNode.get("title").asText();
                double popularity = movieNode.get("popularity").asDouble();
                String releaseDate = movieNode.get("release_date").asText();

                pstmt.setInt(1, id);
                pstmt.setString(2, title);
                pstmt.setDouble(3, popularity);
                pstmt.setString(4, releaseDate);
                pstmt.executeUpdate();

                count++;
            }
        }
        System.out.println("Inserted " + count + " records into the 'movies' table.");
        return count;
    }

    private static void writeSummaryFile(int recordsInserted) {
        String filename = "tmdb_summary.txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = dtf.format(LocalDateTime.now());

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("TMDb Data Load Summary\n");
            writer.write("======================\n");
            writer.write("Load Date: " + now + "\n");
            writer.write("Records Inserted: " + recordsInserted + "\n");
            System.out.println("Summary written to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing summary file: " + e.getMessage());
        }
    }
}
