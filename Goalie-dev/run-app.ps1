# Goalie Application Launcher - UC
Set-Location $PSScriptRoot # UC
Write-Host "Starting Goalie Application..." -ForegroundColor Green # UC
Write-Host "" # UC
try { # UC
    .\mvnw.cmd spring-boot:run # UC
} catch { # UC
    Write-Host "Error occurred: $_" -ForegroundColor Red # UC
} finally { # UC
    Write-Host "" # UC
    Write-Host "Application has stopped. Press any key to exit..." -ForegroundColor Yellow # UC
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") # UC
} # UC

