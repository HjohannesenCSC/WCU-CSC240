
public class Movie {
    //ID of the movie we're looking up
    private int id;
    //Name of the movie
    private String title;
    //gets the score of popularity to give it a rating
    private double popularity;
    // matches Tier 1 JSON/DB field
    private String release_date;

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
