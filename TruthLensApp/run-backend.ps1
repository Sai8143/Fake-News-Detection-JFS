Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "  Starting TruthLens Spring Boot Java Full Stack Backend..." -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Cyan

if (-not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    if (Test-Path "C:\Program Files\Java\jdk-21.0.10") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
    } elseif (Test-Path "C:\Program Files\Java\latest") {
        $env:JAVA_HOME = "C:\Program Files\Java\latest"
    } elseif (Test-Path "C:\Program Files\Java\jdk-17") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
    }
}

Write-Host "Using JAVA_HOME: $env:JAVA_HOME" -ForegroundColor DarkGray
Set-Location -Path "$PSScriptRoot\backend"
& ".\mvnw.cmd" spring-boot:run
