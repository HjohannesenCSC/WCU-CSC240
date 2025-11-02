# WCU-CSC 240 - Project Phase 2
## Hooria, Rylee, Henry, Matt


## To build Phase 1 using Maven:
Build command: mvn -f "c:\GitHub\WCU-CSC240\phase1-ingestor\pom.xml" -DskipTests clean package

Executable jar should now exist: c:\GitHub\WCU-CSC240\phase1-ingestor\target\phase1-ingestor-1.0-SNAPSHOT.jar

Run the created jar: 
java -jar "c:\GitHub\WCU-CSC240\phase1-ingestor\target\phase1-ingestor-1.0-SNAPSHOT.jar"

## Option 2: Compile manually

javac -cp "lib/*" -d target/classes "phase1/TMDBLoader.java" "phase1/NewsAPILoader.java" "phase1/NewsMention.java"

java -cp "target/classes;lib/*" TMDBLoader			
java -cp "target/classes;lib/*" NewsAPILoader

__________________________________________________________________________________________________________________________


## Tier 1: Data API - COMPLETE ✅

### Overview 
The Data API provides direct access to the SQLite database with REST endpoints returning raw JSON data.
### How to Run the Data API
1. Navigate to the data-api folder: src -> main -> java -> DataApi.java
2. Compile the java application:
javac-cp".:../lib/sqlite-jdbc.jar:../lib/slf4j-api-1.7.36.jar:../lib/slf4j-simple-1.7.36.jar" DataApi.java
3. Run the Data API:
javac-cp".:../lib/sqlite-jdbc.jar:../lib/slf4j-api-1.7.36.jar:../lib/slf4j-simple-1.7.36.jar" DataApi
4. The API will start on: http://localhost:4567

Note: If above commands don't work, try the following:
 - Navigate to data-api folder (example: cd 'C:\GitHub\WCU-CSC240\data-api')
 - javac -cp '..\lib\sqlite-jdbc-3.50.3.0.jar' DataApi.java
 - java -cp ".;..\lib\sqlite-jdbc-3.50.3.0.jar" DataApi
   
## Data API Endpoints

| Endpoint       | Method | Purpose                            |
|----------------|--------|------------------------------------|
| `/health`      | GET    | Health check                       |
| `/movies`      | GET    | Get all movies from database       |
| `/movies/{id}` | GET    | Get movie by ID from database      |
| `/news`        | GET    | Get all news mentions from database |
| `/news/{id}`   | GET    | Get news by ID from database       |

---

Tested the API with these curl commands:

curl http://localhost:4567/health

curl http://localhost:4567/movies

curl http://localhost:4567/news

curl http://localhost:4567/movies/1

curl http://localhost:4567/news/1

## Tier 2: Class API - COMPLETE ✅

### Overview

The Class API serves as the middle layer between the Data API and the UI

### How to Run the Class API

Navigate to the project folder containing your pom.xml file:


Compile and build using Maven in a terminal using command: mvn clean compile


# Run the Class API:

mvn exec:java -Dexec.mainClass=com.yourteam.classapi.ClassApi


The API will start on:
http://localhost:8082

⚠️ Make sure the Tier 1 (Data API) is already running on port 4567 — the Class API depends on it for retrieving movie and news data.

⚠️ Also make sure you have seperate terminals for each tier of commands - one for Tier 1, another for Tier 2, and likely a third for Tier 3.

Class API Endpoints
Endpoint	Method	Purpose
/health	GET	Health check for the Class API
/movie-summary/{id}	GET	Combines data from /movies/{id} and /news into one JSON summary
/movies/with-news?minMentions=10&limit=20	GET	Returns a filtered list of movies that have at least the specified number of news mentions
/highlights/top-mentioned?limit=5	GET	Returns the top N movies ranked by news mentions and popularity
/	GET	Returns service information and available endpoints
Example Test Commands
✅ Health Check
curl http://localhost:8082/health


Expected Output:

{"status":"Class API Tier 2 is working!"}

Get Movie Summary
curl http://localhost:8082/movie-summary/1

Get Movies With News Mentions
curl "http://localhost:8082/movies/with-news?minMentions=5&limit=10"

Get Top Mentioned Highlights
curl "http://localhost:8082/highlights/top-mentioned?limit=5"

Notes

The Class API listens on port 8082.

It acts as the middle service layer between:

Tier 1 (Data API) -> Port 4567

Tier 2 (Class API) ->

Tier 3 (UI API) -> Port 8083

If you see an error like:

{"error":"Failed to reach Data API"}, make sure your Data API is running and accessible at http://localhost:4567.

---
## Tier 3: UI API - COMPLETE ✅

### Overview
The UI API consumes the Class API and formats data specifically for frontend/UI consumption, adding UI-specific metadata and aggregation.

### How to Run the UI API 
mvn exec:java -Pui-api
The API will start on: http://localhost:8083
### UI API Endpoints 
| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/health` | GET | Service health check and port status |
| `/dashboard` | GET | Aggregated data for UI dashboard (featured + recent movies) |
| `/movie/{id}` | GET | Movie details formatted for user interface |
| `/movies` | GET | Movies list with UI pagination metadata |
| `/status` | GET | Class API connectivity status check |

### Test the UI API
curl http://localhost:8083/health

curl http://localhost:8083/dashboard

curl http://localhost:8083/movie/1

curl http://localhost:8083/movies

curl http://localhost:8083/status
