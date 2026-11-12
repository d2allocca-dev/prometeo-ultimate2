# Creare l'APK di PROMETEO ULTIMATE su Windows

## Requisiti
- Android Studio aggiornato.
- Android SDK Platform 35 e Android SDK Build-Tools installati da **Tools > SDK Manager**.
- JDK 17 selezionato in **File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.
- Connessione Internet al primo sync Gradle per scaricare plugin e dipendenze.

## Apertura e compilazione
1. Estrai l'intero ZIP in una cartella locale, ad esempio `C:\Progetti\PROMETEO_ULTIMATE`.
2. In Android Studio scegli **Open** e seleziona la cartella che contiene `settings.gradle.kts`.
3. Attendi il completamento di **Gradle Sync**. Se compare una richiesta per installare SDK o Build Tools, accetta.
4. Seleziona la configurazione `androidApp`.
5. Esegui **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Al termine usa il collegamento **locate** nella notifica di Android Studio. Il percorso tipico è `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.
7. Copia l'APK sul telefono e installalo. Per installarlo potrebbe essere necessario autorizzare temporaneamente l'installazione da quella sorgente.

## Build da terminale
Apri il terminale nella cartella del progetto e, se Gradle è installato e configurato nel PATH, esegui:

```powershell
gradle :androidApp:assembleDebug
```

L'APK di debug viene normalmente creato in `androidApp/build/outputs/apk/debug/`.

## Per una release distribuibile
- Non distribuire l'APK debug.
- Configurare una chiave di firma release conservata in modo sicuro fuori dal repository.
- Configurare backend, autenticazione server-side e gestione dei segreti prima di usare l'area riservata con dati reali.
- Eseguire test su almeno un dispositivo Android reale e verificare installazione, avvio, flusso iniziale, chat, persistenza, accesso riservato e blocco sessione.

## Limiti della build attuale
Questo pacchetto contiene sorgenti di sviluppo. La schermata Home è stata ridisegnata prendendo come riferimento il concept grafico consegnato. I moduli Domotica, Droni, Meteo, integrazioni AI e diverse schede operative restano in parte dimostrativi/non collegati. L'accesso riservato è locale e non costituisce ancora autenticazione server-side. Non dichiarare la build pronta per produzione.
