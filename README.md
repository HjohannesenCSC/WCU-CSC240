# WCU-CSC 240 - Project Phase 2
## Hooria, Rylee, Henry, Matt

## Tier 1: Data API - COMPLETE ✅

### Overview 
The Data API provides direct access to the SQLite database with REST endpoints returning raw JSON data.
### How to Run the Data API
1. Navigate to the data-api folder:
cd data-api
2. Compile the java application:
javac-cp".:../lib/sqlite-jdbc.jar:../lib/slf4j-api-1.7.36.jar:../lib/slf4j-simple-1.7.36.jar" DataApi.java
3. Run the Data API:
javac-cp".:../lib/sqlite-jdbc.jar:../lib/slf4j-api-1.7.36.jar:../lib/slf4j-simple-1.7.36.jar" DataApi
4. The API will start on: http://localhost:4567
   
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
