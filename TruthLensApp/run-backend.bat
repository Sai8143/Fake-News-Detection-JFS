@echo off
echo ================================================================
echo   TruthLens - Spring Boot Java Full Stack Backend
echo ================================================================

if not exist "%JAVA_HOME%\bin\java.exe" (
    if exist "C:\Program Files\Java\jdk-21.0.10" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
    ) else if exist "C:\Program Files\Java\latest" (
        set "JAVA_HOME=C:\Program Files\Java\latest"
    ) else if exist "C:\Program Files\Java\jdk-17" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)

echo Using JAVA_HOME: %JAVA_HOME%
cd /d "%~dp0backend"
call mvnw.cmd spring-boot:run
pause
