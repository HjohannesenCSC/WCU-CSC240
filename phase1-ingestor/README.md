# phase1-ingestor

Small utility module that fetches movie data from TMDb and counts news mentions using NewsAPI. This module is a lightweight ingestor that populates a local SQLite database (`project.db`) and writes small summary files (`tmdb_summary.txt`, `newsapi_summary.txt`).

## Build
From PowerShell (run in repository root or anywhere):

mvn -f "c:\GitHub\WCU-CSC240\phase1-ingestor\pom.xml" -DskipTests clean package
```

## Executable jar should now exist:
c:\GitHub\WCU-CSC240\phase1-ingestor\target\phase1-ingestor-1.0-SNAPSHOT-shaded.jar
```

## Run
Run the shaded jar with:
```powershell
# from any folder
java -jar "c:\GitHub\WCU-CSC240\phase1-ingestor\target\phase1-ingestor-1.0-SNAPSHOT-shaded.jar"
```

The program will run the TMDb loader first (populates `movies` table) and then the NewsAPI loader (builds `news_mentions` table and writes `newsapi_summary.txt`).
