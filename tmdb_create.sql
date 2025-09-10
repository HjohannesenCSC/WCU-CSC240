DROP TABLE IF EXISTS tmdb_movies;

CREATE TABLE tmdb_movies (
    tmdb_id TEXT PRIMARY KEY,
    title TEXT,
    year TEXT,
    director TEXT,
    rating REAL
);



