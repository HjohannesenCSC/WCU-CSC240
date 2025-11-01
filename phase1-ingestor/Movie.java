public class Movie {
    private int id;
    private String title;
    private double popularity;
    private String releaseDate;

    public Movie() {}

    public Movie(int id, String title, double popularity, String releaseDate) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() 
    { return title; }
    public void setTitle(String title) 
    { this.title = title; }

    public double getPopularity() 
    { return popularity; }
    public void setPopularity(double popularity) 
    { this.popularity = popularity; }

    public String getReleaseDate() 
    { return releaseDate; }
    public void setReleaseDate(String releaseDate) 
    { this.releaseDate = releaseDate; }

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", title='" + title + '\'' + ", popularity=" + popularity + ", releaseDate='" + releaseDate + '\'' + '}';
    }
}
