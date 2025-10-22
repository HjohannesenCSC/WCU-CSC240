
public class MovieSummary {

    // Movie core
    private int id;
    private String title;
    private double popularity;
    private String releaseDate; // camelCase normalization

    // News aggregation
    private String newsSource;
    private int mentionCount;
    private String newsLastUpdated;

    // Derived business logic
    private String popularityBucket; // LOW/MEDIUM/HIGH
    private boolean trending;

    public static MovieSummary from(Movie m, NewsMention nm) {
        MovieSummary s = new MovieSummary();
        s.id = m.getId();
        s.title = m.getTitle();
        s.popularity = m.getPopularity();
        s.releaseDate = m.getRelease_date();

        if (nm != null) {
            s.newsSource = nm.getNews_source();
            s.mentionCount = nm.getMention_count() == null ? 0 : nm.getMention_count();
            s.newsLastUpdated = nm.getLast_updated();
        } else {
            s.newsSource = null;
            s.mentionCount = 0;
            s.newsLastUpdated = null;
        }

        // Simple business rules (customize as needed)
        s.popularityBucket = s.popularity >= 100 ? "HIGH"
                             : s.popularity >= 50 ? "MEDIUM" : "LOW";
        s.trending = (s.mentionCount >= 25) || (s.mentionCount >= 10 && s.popularity >= 50);
        return s;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPopularity() { return popularity; }
    public String getReleaseDate() { return releaseDate; }
    public String getNewsSource() { return newsSource; }
    public int getMentionCount() { return mentionCount; }
    public String getNewsLastUpdated() { return newsLastUpdated; }
    public String getPopularityBucket() { return popularityBucket; }
    public boolean isTrending() { return trending; }
}
