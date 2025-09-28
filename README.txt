Prereqs:

- SQLite JDBC and Jackson jars included in `lib/` folder inside of project directory
  - `sqlite-jdbc-*.jar` 	- sqlite-jdbc-3.50.3.0.jar
  - `jackson-core` 		- jackson-core-2.20.0.jar
  - `jackson-databind` 		- jackson-databind-2.20.0.jar
  - `jackson-annotations` 	- jackson-annotations-3.0-rc5



1. Compile all Java sources together (classpath includes `lib`):
Command: javac -cp "lib/*" *.java


2. Run TMDB loader (calls the live TMDb API and write `tmdb_summary.txt`): 
    

Command: java -cp ".;lib/*" TMDBLoader
If running on Mac: java -cp ".:lib/*" TMDBLoader


3. Run NewsAPI loader (calls live NewsAPI and write `newsapi_summary.txt`):
            BEWARE : running NewsAPILoader too many times results in Error 429 aka exceeding the Rate Limit

Command: java -cp ".;lib/*" NewsAPILoader
If running on Mac: java -cp ".:lib/*" NewsAPILoader


4. Run verifiers to check summaries txt files against database:

java -cp ".;lib/*" TMDBVerifier      Mac: java -cp ".:lib/*" TMDBVerifier
java -cp ".;lib/*" NewsAPIVerifier   Mac: java -cp ".:lib/*" NewsAPIVerifier


Troubleshooting:
- To reset DB schema, run the SQL scripts with SQLite client

Powershell Command:

sqlite3 project.db < create_movies_table.sql
sqlite3 project.db < create_news_mentions_table.sql


