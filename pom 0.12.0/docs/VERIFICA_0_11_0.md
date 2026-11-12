# Verifica consegna 0.11.0-dev

## Controlli effettuati
- Verificata la presenza dei moduli Gradle `shared`, `androidApp` e `desktopApp`.
- Verificata la presenza dell'Activity Android nativa e del manifest launcher.
- Verificata la configurazione Android (applicationId `it.prometeo.ultimate`, minSdk 26, compileSdk/targetSdk 35).
- Aggiunti e persistiti i campi COSMO per email amministrativa, endpoint cloud, enrollment, DJI, D-Flight e domotica.
- L'email `promrtro_ai-cloud@gmail.com` è preimpostata nella configurazione COSMO e può essere modificata.
- Aggiunte istruzioni esplicite per non includere password o token nell'APK.
- Aggiornata la versione a `0.11.0-dev` e mantenuto il workflow GitHub Actions per la build APK debug.

## Non verificato / non operativo
- La compilazione Gradle/Android non è stata eseguita: nell'ambiente non sono presenti Gradle e Android SDK.
- Non è stato generato né firmato un APK in questa sessione.
- La schermata COSMO e il gate credenziali restano locali e non costituiscono un controllo di accesso server-side.
- Il backend cloud, AI remota, enrollment reale, comandi globali, MFA, email OAuth, DJI/D-Flight e connettori domotici non sono implementati/collegati in questa consegna.
- I campi endpoint salvano configurazioni ma non provano né attivano i servizi.

## Criterio di rilascio
La build è da considerarsi pronta per test tecnici solo dopo esito `success` del workflow GitHub Actions, installazione su un dispositivo Android reale e collaudo delle schermate. Non considerare questa versione idonea alla distribuzione pubblica o alla gestione di unità reali finché i requisiti backend e sicurezza non sono completati.
