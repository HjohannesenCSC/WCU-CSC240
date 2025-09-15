import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TMDBLoader {

    public static void main(String[] args) {
        
        System.out.println("Starting TMDb data load...");

        try {
             Class.forName("org.sqlite.JDBC"); // <-- ADD THIS LINE
        } catch (ClassNotFoundException e) {
             System.out.println("SQLite JDBC driver not found.");
             e.printStackTrace();
             return; // Stop the program if the driver is missing
        }

        String dbUrl = "jdbc:sqlite:project.db";

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            
            System.out.println("Connected to the database.");
            deleteOldData(conn);
            System.out.println("Data load finished successfully.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteOldData(Connection conn) {
        try {
            String sql = "DELETE FROM movies;";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println("Deleted all old records from the 'movies' table.");

        } catch (Exception e) {
            System.out.println("Error during delete: " + e.getMessage());
        }
    }
}