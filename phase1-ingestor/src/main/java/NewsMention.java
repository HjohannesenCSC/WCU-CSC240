public class NewsMention {
    private final int movieId;
    private final String newsSource;
    private final int mentionCount;
    private final String lastUpdated;

    public NewsMention(int movieId, String newsSource, int mentionCount, String lastUpdated) {
        this.movieId = movieId;
        this.newsSource = newsSource;
        this.mentionCount = mentionCount;
        this.lastUpdated = lastUpdated;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public int getMentionCount() {
        return mentionCount;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
