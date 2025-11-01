import java.util.Objects;

/**
 * Simple orchestrator to run the TMDB and NewsAPI loaders in sequence.
 * The existing loader classes (TMDBLoader and NewsAPILoader) already expose a main method,
 * so this class delegates to them. Keep this class in the default package to match the
 * other classes in this module.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Phase1 ingestor: starting TMDb loader...");

        try {
            // TMDBLoader has its own main; call it to fetch and insert movies
            TMDBLoader.main(args == null ? new String[0] : args);
        } catch (Throwable t) {
            System.err.println("TMDBLoader failed: " + t.getMessage());
            t.printStackTrace();
        }

        System.out.println("Phase1 ingestor: starting NewsAPI loader...");
        try {
            // NewsAPILoader also has a main method; call it to build news_mentions
            NewsAPILoader.main(args == null ? new String[0] : args);
        } catch (Throwable t) {
            System.err.println("NewsAPILoader failed: " + t.getMessage());
            t.printStackTrace();
        }

        System.out.println("Phase1 ingestor: finished.");
    }
}
