import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class NewsAPILoader {

    private static final String NEWS_API_KEY = "cf682292185249b1ae22d4af57c2acfd";
    private static final String DB_URL = "jdbc:sqlite:project.db";

    public static void main(String[] args) {
        System.out.println("Starting NewsAPI data load...");
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

                // Ensure the news_mentions table exists, then clean it
                createNewsMentionsTable(conn);
                cleanupTable(conn);

                // Get all movies from the database
                // For each movie, get news count and build a list of NewsMention objects, then insert
                List<NewsMention> mentions = buildNewsMentions(conn);
                int recordsProcessed = insertNewsMentions(conn, mentions);

                // Write summary file
                writeSummaryFile(recordsProcessed);

                System.out.println("NewsAPI data load finished successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createNewsMentionsTable(Connection conn) throws Exception {
    String sql = "CREATE TABLE IF NOT EXISTS news_mentions (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "movie_id INTEGER UNIQUE NOT NULL, " +
        "news_source TEXT NOT NULL, " +
        "mention_count INTEGER, " +
        "last_updated TEXT, " +
        "FOREIGN KEY (movie_id) REFERENCES movies (id)" +
        ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Ensured 'news_mentions' table exists.");
        }
    }

    private static void cleanupTable(Connection conn) throws Exception {
        String sql = "DELETE FROM news_mentions;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Cleaned up existing data in 'news_mentions' table.");
        }
    }

    // Build list of NewsMention objects by querying movies and calling NewsAPI
    private static List<NewsMention> buildNewsMentions(Connection conn) throws Exception {
        String selectSQL = "SELECT id, title FROM movies;";
        List<NewsMention> mentions = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                int movieId = rs.getInt("id");
                String movieTitle = rs.getString("title");

                int newsCount = getNewsCount(movieTitle);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String now = dtf.format(LocalDateTime.now());

                NewsMention nm = new NewsMention(movieId, "NewsAPI", newsCount, now);
                mentions.add(nm);

                System.out.println("Prepared: " + movieTitle + " (" + newsCount + " news mentions)");
            }
        }

        return mentions;
    }

    // Insert a list of NewsMention objects into the DB
    private static int insertNewsMentions(Connection conn, List<NewsMention> mentions) throws Exception {
        String sql = "INSERT INTO news_mentions (movie_id, news_source, mention_count, last_updated) VALUES (?, ?, ?, ?);";
        int count = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (NewsMention nm : mentions) {
                pstmt.setInt(1, nm.getMovieId());
                pstmt.setString(2, nm.getNewsSource());
                pstmt.setInt(3, nm.getMentionCount());
                pstmt.setString(4, nm.getLastUpdated());
                pstmt.executeUpdate();
                count++;
            }
        }
        System.out.println("Inserted " + count + " records into 'news_mentions'.");
        return count;
    }

    private static int getNewsCount(String movieTitle) throws Exception {
        // URL encode the movie title to handle spaces and special characters
        String encodedTitle = URLEncoder.encode(movieTitle, "UTF-8");
        String url = "https://newsapi.org/v2/everything?q=" + encodedTitle + "&apiKey=" + NEWS_API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Parse JSON response to get totalResults
            String jsonResponse = response.body();
            // Simple extraction - we'll look for "totalResults": in the JSON
            int startIndex = jsonResponse.indexOf("\"totalResults\":") + 15;
            int endIndex = jsonResponse.indexOf(",", startIndex);
            String countStr = jsonResponse.substring(startIndex, endIndex);
            return Integer.parseInt(countStr);
        } else {
            System.out.println("NewsAPI request failed for: " + movieTitle + " - Code: " + response.statusCode());
            return 0;
        }
    }



    private static void writeSummaryFile(int recordsProcessed) {
        String filename = "newsapi_summary.txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = dtf.format(LocalDateTime.now());

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("NewsAPI Data Load Summary\n");
            writer.write("==========================\n");
            writer.write("Load Date: " + now + "\n");
            writer.write("Movies Processed: " + recordsProcessed + "\n");
            System.out.println("Summary written to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing summary file: " + e.getMessage());
        }
    }
}
