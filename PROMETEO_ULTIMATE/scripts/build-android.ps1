$ErrorActionPreference = 'Stop'
if (-not (Get-Command gradle -ErrorAction SilentlyContinue)) { throw 'Gradle non trovato. Installa Gradle 8.10.x oppure genera il wrapper.' }
gradle :androidApp:assembleDebug
Write-Host 'APK: androidApp\build\outputs\apk\debug\androidApp-debug.apk'
