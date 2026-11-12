# Rapporto simulazione preliminare 0.5.0

Data: 8 novembre 2026

## Esito

4/4 test logici automatizzati superati:
1. round-trip della serializzazione locale con separatori, backslash, testo accentato, emoji e stringa vuota;
2. ciclo di primo avvio: l'attivazione non è consentita prima del completamento della configurazione;
3. matrice ruoli: ZEUS unico ruolo autorizzato a concedere ruoli master; ADE/POSEIDONE possono amministrare amministratori; un ADMIN entra nell'area riservata solo con approvazione esplicita;
4. validazione di base dell'endpoint: HTTPS richiesto e host non vuoto.

## Limiti della simulazione

I test sono una simulazione Python indipendente della logica prevista, non test eseguiti sul bytecode Kotlin, su Android, su backend o su un dispositivo. La matrice AccessPolicy nel client è un ausilio per l'interfaccia e NON è un confine di sicurezza. Ogni autorizzazione riservata deve essere ripetuta e verificata lato server con identità autenticata, ruoli firmati, audit e revoca delle sessioni.

## Non simulato / non integrato

- AI generativa reale: richiede endpoint backend operativo e credenziali server non fornite.
- Login/MFA e gestione utenti reali: richiedono backend e configurazione del proprietario.
- ORACLE semantico/sincronizzato: non implementato; le note locali non sono memoria cognitiva.
- Domotica e droni: richiedono account, API ufficiali, permessi e dispositivi.
- Offline AI: richiede un modello locale compatibile, distribuzione e valutazione delle risorse del dispositivo.

Compilazione Gradle, test Android strumentali e test su dispositivo non eseguiti.
