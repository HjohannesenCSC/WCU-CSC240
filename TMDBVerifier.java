import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TMDBVerifier {

    private static final String DB_URL = "jdbc:sqlite:project.db";
    private static final String SUMMARY_FILE = "tmdb_summary.txt";

    public static void main(String[] args) {
        System.out.println("TMDB Verifier starting...");

        int summaryCount = readSummaryCount();
        if (summaryCount < 0) {
            System.out.println("Failed to read summary file. Aborting.");
            return;
        }

        int dbCount = queryMoviesCount();
        if (dbCount < 0) {
            System.out.println("Failed to query database. Aborting.");
            return;
        }

        System.out.println("Summary reports recordsInserted = " + summaryCount);
        System.out.println("Database contains rows = " + dbCount);

        if (summaryCount == dbCount) {
            System.out.println("SUCCESSFULLY VERIFIED: counts match.");
        } else {
            System.out.println("VERIFICATION FAILED: counts differ.");
        }
    }

    private static int readSummaryCount() {
        try (BufferedReader br = new BufferedReader(new FileReader(SUMMARY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Records Inserted:")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        return Integer.parseInt(parts[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading summary file: " + e.getMessage());
        }
        return -1;
    }

    private static int queryMoviesCount() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            return -1;
        }

        String sql = "SELECT COUNT(*) AS cnt FROM movies;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            System.out.println("Database query error: " + e.getMessage());
        }
        return -1;
    }
}
