@echo off
@title 1.build after code change

@Echo -----------------------------------------------------------
@Echo Build start
@Echo -----------------------------------------------------------

cd policy\personal-auto
call mvn clean install -Dmaven.test.skip=true
cd policy\applications\auto
call mvn clean install -Dmaven.test.skip=true
cd policy\applications\generic
call mvn clean install -Dmaven.test.skip=true

@Echo -----------------------------------------------------------
@Echo Build docker
@Echo -----------------------------------------------------------
cd docker\dev
call docker-compose build


@Echo -----------------------------------------------------------
@Echo Deployer docker
@Echo -----------------------------------------------------------
cd docker\dev
call docker-compose up policy_generic_deployer 
call docker-compose up policy_auto_deployer

@Echo -----------------------------------------------------------
@Echo start
@Echo -----------------------------------------------------------
cd docker\dev
call docker-compose up -d policy_auto_commands policy_auto_facade policy_generic_events policy_generic_commands policy_generic_facade

pause
