# PROMETEO ULTIMATE — specifica di implementazione

## Regole di prodotto
- ULTIMATE ed EVOLUTION restano due app distinte. Questa repository riguarda esclusivamente ULTIMATE.
- Primo avvio limitato; l'assistente viene attivato solo con azione esplicita dell'utente.
- Il nome dell'assistente è personalizzabile.
- L'interfaccia destinata agli utenti ordinari non deve esporre riferimenti a funzioni riservate. Le funzioni riservate non si proteggono nascondendo pulsanti: ogni richiesta deve essere autorizzata dal server.
- ZEUS è il solo ruolo che può nominare/revocare delegati e modificare la gerarchia. ADE e Poseidone possono amministrare utenti amministrativi, ma non elevare se stessi né modificare ZEUS. Un amministratore ordinario può accedere a dati/azioni riservati solo con approvazione esplicita e revocabile di ZEUS.

## Moduli e confini
- Assistente: client conversazionale; le risposte locali sono dichiarate come locali. Per generazione AI si usa un backend HTTPS, mai una chiave API incorporata nell'APK.
- ORACLE: archivio personale; separare note, preferenze e memoria derivata. Consenso, esportazione, cancellazione e retention sono obbligatori. Non salvare password, PIN, token o segreti.
- EFESTO: pannello configurazione; le modifiche privilegiate richiedono autenticazione forte, autorizzazione server-side e audit.
- Dispositivi/domotica: connettori a capability minima, consenso per singolo dispositivo, revoca e registro operazioni.
- Droni: pianificazione e consultazione solo con dati aeronautici ufficiali aggiornati; l'app non deve promettere autorizzazioni al volo né comandare il drone senza SDK/API ufficiale e controlli di sicurezza.
- Offline: note, attività e preferenze locali. Le risposte generative e i dati live esterni non sono disponibili offline salvo un modello locale esplicitamente installato.

## Persistenza attualmente aggiunta
La versione sorgente include un adapter `PlatformStore`: Android usa SharedPreferences, desktop usa Java Preferences. Vengono conservati localmente configurazione iniziale, nome assistente, preferenza offline, note, attività e cronologia chat. È una persistenza locale di comodità, non un vault cifrato e non è adatta a segreti o dati altamente sensibili.

## Contratto backend necessario per la produzione
- `POST /v1/session`: login con OIDC/passkey o password gestita dal provider; MFA per ruoli privilegiati.
- `POST /v1/assistant/respond`: sessione autenticata, limiti di utilizzo, moderazione e provider AI configurato lato server.
- `GET/PUT/DELETE /v1/oracle`: dati per proprietario, consenso, cifratura server-side, esportazione e cancellazione.
- `GET/POST /v1/admin/users`: policy RBAC/ABAC verificata server-side.
- `POST /v1/admin/delegates`: ZEUS only; approvazione, revoca e audit.
- `POST /v1/config/publish`: configurazioni firmate, versionate e con rollback.
- Tutti gli endpoint: TLS, token brevi, refresh rotation, rate limit, protezione replay, audit append-only, secret manager, backup cifrati.

## Stato reale
Questa consegna implementa la persistenza locale dei dati di base e mantiene il flusso iniziale/attivazione. Le schermate di moduli esterni restano descrittive: non esistono ancora connettori domotici, SDK droni o backend AI configurato. Non è implementata una vera coscienza/senzienza; si può progettare un agente adattivo con memoria controllata, strumenti autorizzati e supervisione. Non dichiarare funzioni attive finché non collegate e verificate.

## Configurazioni richieste al proprietario prima della produzione
- Dominio HTTPS e backend gestito.
- Provider AI e relative credenziali inserite esclusivamente nel secret manager del backend.
- Provider identità, MFA e account ZEUS creato con procedura di bootstrap sicura.
- Informativa privacy, consenso memoria, retention e processo di cancellazione.
- Credenziali e PIN già condivisi in conversazioni precedenti vanno sostituiti e non riutilizzati.
