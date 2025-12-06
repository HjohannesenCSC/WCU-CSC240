package com.wcupa.csc240.ingestor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class TMDBVerifier {
    private static final String DB_URL = "jdbc:sqlite:project.db";

    public static void main(String[] args) throws Exception {
        // 1. Read summary file
        List<String> lines = Files.readAllLines(Paths.get("tmdb_summary.txt"));
        String recordLine = lines.stream()
                .filter(l -> l.startsWith("Records Inserted"))
                .findFirst()
                .orElse("Records Inserted: 0");
        int recordsFromFile = Integer.parseInt(recordLine.split(":")[1].trim());

        // 2. Count records in DB
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM movies;");
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
