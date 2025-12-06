package com.wcupa.csc240.model;


public class NewsMention {
    private int id;                // PK in news_mentions
    private int movie_id;          // FK -> movies.id
    private String news_source;
    private Integer mention_count; // nullable-safe
    private String last_updated;

    public NewsMention() {}

    public int getId() { return id; }
    public int getMovie_id() { return movie_id; }
    public String getNews_source() { return news_source; }
    public Integer getMention_count() { return mention_count; }
    public String getLast_updated() { return last_updated; }

    public void setId(int id) { this.id = id; }
    public void setMovie_id(int movie_id) { this.movie_id = movie_id; }
    public void setNews_source(String news_source) { this.news_source = news_source; }
    public void setMention_count(Integer mention_count) { this.mention_count = mention_count; }
    public void setLast_updated(String last_updated) { this.last_updated = last_updated; }
}
