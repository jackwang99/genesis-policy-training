@echo off
@title 2 build docker

@Echo -----------------------------------------------------------
@Echo Build docker images (after code change)
@Echo -----------------------------------------------------------

cd docker\dev
call docker-compose build

pause
