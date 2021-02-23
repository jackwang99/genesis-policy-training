@echo off
@title 1.build

@Echo -----------------------------------------------------------
@Echo Build start
@Echo -----------------------------------------------------------

call mvn clean install -Dmaven.test.skip=true

pause
