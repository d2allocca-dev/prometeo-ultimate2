# COSMO — configurazione iniziale 0.11.0

L'app preimposta il recapito `promrtro_ai-cloud@gmail.com`. Può essere cambiato dalle impostazioni. La schermata COSMO consente di aggiungere endpoint HTTPS per il backend centrale, registrazione delle unità, DJI/D-Flight e domotica.

I campi vengono salvati localmente come configurazione non segreta. Non inserire password, token o chiavi API in tali campi.

## Per attivare il controllo globale

1. Registrare la casella email e attivare MFA sul provider.
2. Realizzare un backend HTTPS indipendente dall'app.
3. Collegare il backend al provider email tramite OAuth 2.0 o identità di servizio; custodire le credenziali esclusivamente in un secret manager server-side.
4. Implementare enrollment con identità univoca per unità, certificati/chiavi dispositivo, revoca e rotazione.
5. Firmare i comandi e verificare firma, scadenza, nonce e autorizzazione prima dell'esecuzione.
6. Applicare RBAC lato server: ZEUS root; ADE e POSEIDONE delegati revocabili da ZEUS; amministratori limitati al proprio ambito; COSMO negato per default salvo approvazione esplicita.
7. Registrare audit append-only e sottoporre il sistema a test di sicurezza.

L'email non deve essere usata come password, API, canale di controllo diretto o identità sufficiente per autorizzare comandi. L'APK non deve contenere segreti condivisi.
