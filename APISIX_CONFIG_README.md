## APISIX config.yaml — how to use with a running APISIX container

How to view, edit, and apply the `config.yaml` for an APISIX instance that's already running in Docker on Windows.
Assumption: APISIX container is named `docker-apisix-apisix-1` 

Paths
- Container config path: `/usr/local/apisix/conf/config.yaml`
- Repo copy (local): `config.yaml` (root of repo)

Quick summary
- To view the active config: `docker exec <container> cat /usr/local/apisix/conf/config.yaml`.
- To edit: copy the repo `config.yaml` into the container then restart the container.
- To make runtime changes without restarting: use the APISIX Admin API (requires `admin_key`).
- To test:
```powershell
curl.exe -i http://127.0.0.1:9080/data/news
curl.exe -i http://127.0.0.1:9080/data/movies
```

Prerequisites
- Docker running on Windows (Docker Desktop). Link: https://docs.docker.com/desktop/setup/install/windows-install/ 
- APISIX Installation guide: https://apisix.apache.org/docs/apisix/installation-guide/
```powershell
    git clone https://github.com/apache/apisix-docker.git
    cd apisix-docker/example
```
- Inspect containers (shows APISIX and ports)

docker ps --format '{{.ID}}\t{{.Names}}\t{{.Ports}}\t{{.Image}}'

    - should see docker-apisix-prometheus-1, grafana-1, etcd-1, web1-1 with ID's and ports listed

- APISIX container running and reachable on the mapped host ports (proxy: `9080`, admin: `9180`).
- Admin API found in `config.yaml` under `deployment.admin.admin_key`
______________________________________________________________________________________________________________________
## Updating the config.yaml file:

1) View current config inside the container
Open PowerShell and run:
```powershell
docker exec docker-apisix-apisix-1 cat /usr/local/apisix/conf/config.yaml
```

2) Back up container's existing config
```powershell
# copy container config to local backup
docker cp docker-apisix-apisix-1:/usr/local/apisix/conf/config.yaml .\config.yaml.apisix.backup
```

3) Copy edited `config.yaml` from the repo into the container and restart

Note: overwriting a file that's in-use may show messages like "device or resource busy" but the copy can still succeed. Back up first.
```powershell
# copy edited file into container
docker cp .\config.yaml docker-apisix-apisix-1:/usr/local/apisix/conf/config.yaml

# restart the container so APISIX re-reads file on startup
docker restart docker-apisix-apisix-1
```

5) Test the proxy
From the host, call APISIX proxy port (usually `9080`).

```powershell
curl.exe -i http://127.0.0.1:9080/data/news
curl.exe -i http://127.0.0.1:9080/data/movies
```
If route is configured with proxy-rewrite, APISIX will forward `/data/news` to `http://host.docker.internal:4567/news` to receive Data API's JSON


## UIApi Routing:
- Make sure UIApi is running on port 8083
'''powershell
curl.exe -i http://127.0.0.1:9080/ui/movies
curl.exe -i http://127.0.0.1:9080/ui/status
'''
- host.docker.internal:8083 serves as upstream so the container reaches host OS service at port 8083
The proxy-rewrite strips /api so:
Client -> APISIX: /ui/movies
APISIX -> UI API upstream: /movies
This matches the endpoints implemented in UiApi.java (which expose /movies, /dashboard, /status, /movie/:id).

## ClassApi Routing:
- Make sure ClassApi is running on port 8082

curl.exe -i http://localhost:8082/health

- First check if its reachable from inside APISIX container
'''powershell
docker exec -it docker-apisix-apisix-1 /bin/sh
curl -i http://host.docker.internal:8082/health
'''
- Test through APISIX (host):

- class API endpoints via APISIX
curl.exe -i http://127.0.0.1:9080/api/health
curl.exe -i http://127.0.0.1:9080/api/movies
curl.exe -i http://127.0.0.1:9080/api/movie-summary/1



6) Troubleshooting tips
- If `curl` returns `404 No context found for request` or `502 Bad Gateway`:
  - Verify the route exists: `curl.exe -i -X GET "http://127.0.0.1:9180/apisix/admin/routes" -H "X-API-KEY: <admin_key>"`

7) Revert changes
```powershell
# restore backup
docker cp .\config.yaml.apisix.backup docker-apisix-apisix-1:/usr/local/apisix/conf/config.yaml
docker restart docker-apisix-apisix-1
```

9) Example: quick verification checklist
- View container config: `docker exec docker-apisix-apisix-1 cat /usr/local/apisix/conf/config.yaml`
- Back up: `docker cp docker-apisix-apisix-1:/usr/local/apisix/conf/config.yaml .\config.yaml.apisix.backup`
- Copy edited file in: `docker cp .\config.yaml docker-apisix-apisix-1:/usr/local/apisix/conf/config.yaml`
- Restart: `docker restart docker-apisix-apisix-1`
- Test: `curl.exe -i http://127.0.0.1:9080/data/news`
---
