@echo off
@title 3.1 infrastructure

@Echo -----------------------------------------------------------
@Echo Start infrastructure docker containers (jaeger, zookeeper, kafka, cassandra, solr)
@Echo -----------------------------------------------------------

cd docker\dev
call docker-compose up -d jaeger zookeeper dse 
timeout /t 20
call docker-compose up -d kafka


pause
