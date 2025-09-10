DROP TABLE IF EXISTS omdb_movies;

CREATE TABLE omdb_movies (
    imdb_id TEXT PRIMARY KEY,
    title TEXT,
    year TEXT,
    director TEXT,
    rating REAL
);



