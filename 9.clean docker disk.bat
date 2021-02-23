@echo off
@title 9.clean docker reset env

@Echo -----------------------------------------------------------
@Echo Clean docker start
@Echo -----------------------------------------------------------

cd docker\dev
docker-compose down
timeout /t 2
docker volume prune -f
timeout /t 2
docker network prune -f


pause
