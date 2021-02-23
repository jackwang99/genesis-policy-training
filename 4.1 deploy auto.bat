@echo off
@title 4.1 deploy auto apps

@Echo -----------------------------------------------------------
@Echo Deploy auto apps
@Echo -----------------------------------------------------------

cd docker\dev
call docker-compose up policy_generic_deployer 
call docker-compose up policy_auto_deployer
 
pause
