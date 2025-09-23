import java.sql.*;
import java.nio.file.*;
import java.io.IOException;

public class loadSQL {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:mydb.db"; // SQLite file-based DB

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // Read SQL file
            String sql = new String(Files.readAllBytes(Paths.get("data.sql")));

            // Execute SQL script (creates table + inserts data)
            stmt.executeUpdate(sql);

            // Query the table
            ResultSet rs = stmt.executeQuery("SELECT * FROM my_table");

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name"));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
