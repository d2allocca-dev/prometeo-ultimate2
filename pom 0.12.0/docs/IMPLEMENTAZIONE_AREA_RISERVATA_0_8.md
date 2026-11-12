# PROMETEO ULTIMATE 0.8 – area riservata e moduli locali

## Modifiche implementate
- Accesso non esposto nella navigazione ordinaria: sette tocchi sul titolo superiore aprono la schermata di autenticazione.
- Primo accesso: creazione di una frase segreta di almeno 12 caratteri e conferma.
- Derivazione della credenziale con PBKDF2-HMAC-SHA256, salt casuale da 128 bit e 180.000 iterazioni; non viene memorizzata la frase in chiaro.
- Area riservata con EFESTO e ORACLE; i due pannelli non compaiono nella navigazione ordinaria.
- EFESTO: nome assistente, lingua, preferenza vocale e preferenza offline persistenti.
- ORACLE: memorie testuali locali, ricerca senza distinzione maiuscole/minuscole, aggiunta e cancellazione.
- Blocco dell'area riservata al ritorno alla schermata principale.
- La pagina Note ordinaria non espone il nome ORACLE.
- La pagina Moduli pubblica non elenca i due pannelli riservati.

## Accesso dell'utente proprietario
1. Aprire PROMETEO ULTIMATE e completare il primo avvio e l'attivazione dell'assistente.
2. Nella schermata principale toccare sette volte il titolo superiore dell'app.
3. Al primo accesso creare una frase segreta di almeno 12 caratteri e confermarla.
4. Gli accessi successivi richiedono la stessa frase.
5. Si apre EFESTO; da lì si può aprire ORACLE. Usare «Blocca area riservata» per chiudere l'area.

## Limiti di sicurezza da non confondere
Questa implementazione protegge la navigazione e la credenziale locale con derivazione lenta. Non equivale a un'autenticazione server, non prova l'identità civile di ZEUS e non protegge da un dispositivo compromesso/rootato, backup ostili o analisi del processo. Il primo utente che inizializza la frase segreta sul dispositivo diventa il proprietario locale: per un rilascio multiutente occorrono bootstrap amministrativo autenticato, backend con RBAC server-side, MFA, revoca sessioni, audit log e protezione delle chiavi tramite Android Keystore.

## Funzioni non realizzabili senza servizi/credenziali esterne
- AI generativa: serve un backend AI raggiungibile e una chiave del provider conservata esclusivamente lato server. L'endpoint inserito nelle impostazioni non crea da solo un servizio AI.
- Sincronizzazione ORACLE: serve backend, account e policy di cifratura end-to-end.
- Google Home/Alexa/eWeLink/Sonoff: servono account sviluppatore, OAuth, API ufficiali e dispositivi compatibili.
- DJI/D-Flight: servono accesso alle API/dati autorizzati, account e verifica delle condizioni normative e geografiche. Non viene dichiarato alcun controllo drone attivo.
- AI offline: serve selezionare, distribuire e validare un modello compatibile con la memoria e le prestazioni dei dispositivi target.

## Verifica eseguita in questa revisione
- Controllo statico delle modifiche e delle rotte UI.
- Controllo che EFESTO/ORACLE non siano presenti nella navigazione ordinaria.
- Controllo della presenza di PBKDF2-HMAC-SHA256, salt casuale e assenza di password hardcoded.
- XML del manifest verificato separatamente.
- Non eseguiti Gradle Sync, build APK, test strumentali Android o test multi-dispositivo in questo ambiente.
