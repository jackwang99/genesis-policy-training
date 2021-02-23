@echo off
@title 4.2 auto apps

@Echo -----------------------------------------------------------
@Echo Start auto apps
@Echo -----------------------------------------------------------

cd docker\dev

call docker-compose up -d policy_auto_commands policy_auto_facade policy_generic_events policy_generic_commands policy_generic_facade

pause
