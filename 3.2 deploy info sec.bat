@echo off
@title 3.2 deploy common components infra

@Echo -----------------------------------------------------------
@Echo Deploy metadata for genesis common components (first time or after components upgrade) infra
@Echo -----------------------------------------------------------

cd docker\dev
call docker-compose up infra_deployer 
call docker-compose up sec_deployer 

 
pause
