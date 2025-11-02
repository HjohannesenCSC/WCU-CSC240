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
