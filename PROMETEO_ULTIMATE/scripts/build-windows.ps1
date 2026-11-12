$ErrorActionPreference = 'Stop'
if (-not (Get-Command gradle -ErrorAction SilentlyContinue)) { throw 'Gradle non trovato. Installa Gradle 8.10.x oppure genera il wrapper.' }
gradle :desktopApp:packageDistributionForCurrentOS
Write-Host 'Controlla desktopApp\build\compose\binaries\main\ per EXE/MSI.'
