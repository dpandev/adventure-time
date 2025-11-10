@echo off
REM Run the Adventure Time client with a specific worldpack
REM Usage: run-client.bat [worldpack-name]
REM Example: run-client.bat jurassic

setlocal

REM Default to example worldpack if no argument provided
set WORLDPACK=%1
if "%WORLDPACK%"=="" set WORLDPACK=example

REM Change to script directory
cd /d "%~dp0"

REM Build the application if needed
echo Building Adventure Time...
call gradlew.bat :client:installDist --quiet --no-configuration-cache

REM Run the application directly from repo root (not through Gradle)
REM Working directory stays in repo root so saves go to .\saves\
client\build\install\client\bin\client.bat --world=%WORLDPACK%

