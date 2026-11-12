# Stato PROMETEO ULTIMATE 0.5.0

## Implementato nel sorgente
- Flusso iniziale limitato e attivazione esplicita.
- Personalizzazione del nome, schermate principali, note/attività/chat locale e persistenza locale già presente.
- Nuovo modello di policy ruoli ZEUS/ADE/POSEIDONE/ADMIN/USER e ciclo di vita assistente.
- Validazione di base dell'endpoint HTTPS.
- Script ripetibile di simulazione logica e rapporto test.

## Parziale / da collegare
- Chat locale: risposte dimostrative, non AI generativa.
- ORACLE: archivio locale di note e conversazioni, senza ricerca semantica o sincronizzazione.
- EFESTO: impostazioni di base, senza gestione privilegiata autenticata.
- Policy ruoli: modello lato client, non sicurezza reale.

## Non implementato
- Backend di produzione, autenticazione, MFA, gestione sessioni e autorizzazioni server.
- AI generativa con endpoint effettivo e gestione sicura dei segreti.
- Integrazioni Google Home/Alexa/Sonoff/eWeLink e DJI/D-Flight.
- Modello AI locale/offline e aggiornamenti firmati.

## Verifiche
- Simulazione Python: 4/4 test superati.
- Integrità ZIP e XML manifest controllati durante la preparazione del pacchetto.
- Gradle build, test Kotlin/Android e prova su dispositivo: non eseguiti.
