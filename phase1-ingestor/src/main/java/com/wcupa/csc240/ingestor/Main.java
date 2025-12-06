package com.wcupa.csc240.ingestor;
/**
 * Orchestrator placed in the Maven standard source tree so the module builds correctly.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Phase1 ingestor: starting TMDb loader...");

        try {
            TMDBLoader.main(args == null ? new String[0] : args);
        } catch (Throwable t) {
            System.err.println("TMDBLoader failed: " + t.getMessage());
            t.printStackTrace();
        }

        System.out.println("Phase1 ingestor: starting NewsAPI loader...");
        try {
            NewsAPILoader.main(args == null ? new String[0] : args);
        } catch (Throwable t) {
            System.err.println("NewsAPILoader failed: " + t.getMessage());
            t.printStackTrace();
        }

        System.out.println("Phase1 ingestor: finished.");
    }
}
