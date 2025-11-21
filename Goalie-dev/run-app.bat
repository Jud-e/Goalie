@echo off
cd /d "%~dp0"
echo Starting Goalie Application...
echo.
mvnw.cmd spring-boot:run
echo.
echo Application has stopped. Press any key to exit...
pause >nul

