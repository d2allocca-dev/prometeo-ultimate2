# Configurazione proprietario — PROMETEO ULTIMATE 0.11.0-dev

## Email globale COSMO
La schermata **COSMO → Configurazione cloud e unità** contiene il campo email amministrativa, preimpostato con:

`promrtro_ai-cloud@gmail.com`

È un valore modificabile e viene conservato nelle preferenze locali del dispositivo. La registrazione effettiva della casella e la verifica della proprietà devono essere svolte dal proprietario presso il provider email. Il solo inserimento nel codice non crea l'account e non autorizza alcuna unità.

## Password della casella
Non è presente un campo per memorizzare la password Gmail nell'app. È intenzionale: l'APK è ispezionabile e può essere copiato; incorporare una password permetterebbe a chiunque di estrarla. Non committare password, app password, token OAuth o chiavi API.

Quando il backend verrà predisposto, usare OAuth 2.0 con consenso esplicito oppure una credenziale applicativa limitata, custodita solo nel secret manager del server. La casella email non deve essere usata come bus di comandi.

## Campi predisposti in COSMO
- Endpoint HTTPS backend centrale.
- Endpoint HTTPS di enrollment/registrazione unità.
- Endpoint servizio DJI/flotta.
- Endpoint D-Flight/pianificazione.
- Endpoint domotica (gateway che integra eWeLink/Sonoff, Google Home o Alexa).

Questi valori sono configurazioni locali: non attivano automaticamente integrazioni. Ogni servizio richiede API ufficiali, credenziali, consensi, gestione errori e test.

## Requisiti minimi del backend globale
1. Identità e MFA per ruoli privilegiati.
2. ZEUS unico soggetto autorizzato a nominare/revocare ADE e Poseidone e a modificare la gerarchia.
3. ADE e Poseidone possono amministrare amministratori, ma non elevare sé stessi né modificare ZEUS.
4. Ogni unità si registra con codice di enrollment monouso e ottiene una propria identità/certificato revocabile.
5. Comandi firmati, timestamp/nonce anti-replay, allowlist di azioni, rate limit e audit append-only.
6. Configurazioni distribuite in modo firmato, versionato, con rollback.
7. Segreti in un secret manager; nessun segreto statico nell'APK.

## Passi finali a carico del proprietario
- Registrare la casella email e attivare MFA.
- Pubblicare un backend HTTPS e compilare i relativi endpoint.
- Configurare il provider di identità e creare ZEUS tramite bootstrap sicuro.
- Aggiungere le credenziali dei provider esterni esclusivamente lato server.
- Completare informativa privacy, consenso, retention, esportazione e cancellazione dati.
- Eseguire test di sicurezza e collaudo su dispositivi fisici prima della distribuzione.
