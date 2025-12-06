package com.wcupa.csc240.ingestor;
public class NewsMention {
    private int movieId;
    private String newsSource;
    private int mentionCount;
    private String lastUpdated;

    public NewsMention() {}

    public NewsMention(int movieId, String newsSource, int mentionCount, String lastUpdated) {
        this.movieId = movieId;
        this.newsSource = newsSource;
        this.mentionCount = mentionCount;
        this.lastUpdated = lastUpdated;
    }

    public int getMovieId() 
    { return movieId; }
    public void setMovieId(int movieId) 
    { this.movieId = movieId; }

    public String getNewsSource() 
    { return newsSource; }
    public void setNewsSource(String newsSource) 
    { this.newsSource = newsSource; }

    public int getMentionCount() 
    { return mentionCount; }
    public void setMentionCount(int mentionCount) 
    { this.mentionCount = mentionCount; }

    public String getLastUpdated() 
    { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) 
    { this.lastUpdated = lastUpdated; }

    @Override
    public String toString() {
        return "NewsMention{" + "movieId=" + movieId + ", newsSource='" + newsSource + '\'' + ", mentionCount=" + mentionCount + ", lastUpdated='" + lastUpdated + '\'' + '}';
    }
}
