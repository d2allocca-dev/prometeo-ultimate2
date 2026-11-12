# Stato release 0.12.0-adaptive — 12 novembre 2026

## Consegna
Sorgenti Android/Kotlin con primo livello reale di adattamento hardware, dashboard informativa, test comuni e workflow GitHub Actions.

## Verifiche passate in questa sessione
- Compilazione Kotlin standalone di `AdaptiveRuntime.kt` e smoke test: 4/4 superati.
- Parsing XML del manifest Android: superato.
- Verifica statica della presenza del workflow e del collegamento hardware probe/dashboard: superata.
- Archivio ZIP: controllato con test CRC dopo la creazione.

## Non verificato
- Build Gradle Android completa: non eseguita in questo ambiente, che non dispone di Gradle/Android SDK.
- APK: non generato.
- Test su dispositivo fisico: non eseguiti.
- AI generativa offline con LLM: non implementata; il motore locale attuale è deterministico.
- COSMO server-side/MFA/RBAC, PROMETEO EVOLUTION completa, sincronizzazione cross-device e integrazioni live DJI/D-Flight, eWeLink/Sonoff, Google Home/Alexa: non operative in questa consegna.

## Esito
Questa release aggiunge e testa il livello adattivo hardware, ma NON è una release finale completa e non è corretto affermare che manchi soltanto l'APK. Il workflow GitHub è il prossimo controllo tecnico per validare compilazione e test Android.
