
public class Movie {
    private int id;
    private String title;
    private double popularity;
    private String release_date; // matches Tier 1 JSON/DB field

    public Movie() {}

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPopularity() { return popularity; }
    public String getRelease_date() { return release_date; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPopularity(double popularity) { this.popularity = popularity; }
    public void setRelease_date(String release_date) { this.release_date = release_date; }
}
