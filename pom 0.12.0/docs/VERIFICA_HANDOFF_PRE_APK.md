# Verifica pacchetto di consegna — pre-APK

Data: 2026-11-12

## Verificato in questa preparazione
- Archivio sorgente Android nativo 0.11.0 estratto.
- Esclusi output di build, cache IDE, `.git`, file `local.properties` e keystore.
- Workflow GitHub Actions collocato nel percorso corretto `.github/workflows/android-apk.yml`.
- Workflow aggiornato per installare `platforms;android-35` e `build-tools;35.0.0`; non richiede il vecchio pacchetto SDK `tools`.
- Il workflow richiede test, build APK e verifica dell'esistenza del file prima di caricarlo come artifact.

## Non verificato
- Compilazione Gradle effettiva, test Android su dispositivo, firma release e installazione.
- Completezza funzionale di ogni modulo.
- Integrazioni con servizi o dispositivi esterni.
- Integrazione del tema Zeus nelle schermate effettive.
- Backend di produzione per COSMO e autorizzazioni.

## Esito
Pacchetto sorgente consolidato e predisposto alla build APK; non è un prodotto finito certificato. Il solo APK non può essere l'unica attività residua finché le voci non verificate sopra non sono implementate e collaudate.
