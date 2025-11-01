DROP TABLE IF EXISTS news_mentions;

CREATE TABLE news_mentions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    movie_id INTEGER UNIQUE NOT NULL,
    news_source TEXT NOT NULL,
    mention_count INTEGER,
    last_updated TEXT,
    FOREIGN KEY (movie_id) REFERENCES movies (id)
);
