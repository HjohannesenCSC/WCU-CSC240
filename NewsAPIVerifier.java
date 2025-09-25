import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class NewsAPIVerifier {
    private static final String DB_URL = "jdbc:sqlite:project.db";

    public static void main(String[] args) throws Exception {
        // 1. Read summary file
        List<String> lines = Files.readAllLines(Paths.get("newsapi_summary.txt"));
        String recordLine = lines.stream()
                .filter(l -> l.startsWith("Movies Processed"))
                .findFirst()
                .orElse("Movies Processed: 0");
        int recordsFromFile = Integer.parseInt(recordLine.split(":")[1].trim());

        // 2. Count records in DB
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM news_mentions;");
        rs.next();
        int recordsInDB = rs.getInt(1);

        // 3. Compare
        if (recordsFromFile == recordsInDB) {
            System.out.println("Verification passed! Records match: " + recordsInDB);
        } else {
            System.out.println("Verification failed! File: " + recordsFromFile + ", DB: " + recordsInDB);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
