@echo off
@title 3.3 sec infro

@Echo -----------------------------------------------------------
@Echo Start sec_commands sec_events sec_facade infra_events infra_facade infra_commands
@Echo -----------------------------------------------------------

cd docker\dev
call docker-compose up -d sec_commands sec_events sec_facade infra_events infra_facade infra_commands

pause
