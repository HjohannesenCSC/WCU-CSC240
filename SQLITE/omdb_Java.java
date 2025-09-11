import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;



public class omdb_Java{
    public static void main(String[] args) {
        String apiKey = "YOUR_OMDB_API_KEY";
        String movieTitle = "Cars";
        String urlStr = "http://www.omdbapi.com/?t=" + movieTitle + "&apikey=" + apiKey;

         try {
            // 1. Fetch JSON from OMDb API
            URL url = new URL(urlStr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(jsonBuilder.toString());

            // 2. Connect to SQLite
            Connection conn = DriverManager.getConnection("jdbc:sqlite:movies.db");
            Statement stmt = conn.createStatement();

            // 3. Clear old data
            stmt.executeUpdate("DELETE FROM omdb_movies");

            // 4. Insert new record
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO omdb_movies (imdb_id, title, year, director, rating) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, json.getString("imdbID"));
            ps.setString(2, json.getString("Title"));
            ps.setString(3, json.getString("Year"));
            ps.setString(4, json.getString("Director"));
            ps.setDouble(5, Double.parseDouble(json.getString("imdbRating")));
            ps.executeUpdate();

            // 5. Write summary file
            File dir = new File("output");
            if (!dir.exists()) dir.mkdir();
            PrintWriter summary = new PrintWriter("output/omdb_summary.txt");
            summary.println("Date: " + new java.util.Date());
            summary.println("Records inserted: 1");
            summary.close();

            conn.close();
            System.out.println("✅ OMDb data loaded successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




    
