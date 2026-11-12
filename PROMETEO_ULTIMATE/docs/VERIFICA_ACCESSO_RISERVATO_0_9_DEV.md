# Verifica flusso di accesso riservato — 0.9-dev

## Modifiche
- Rimossa la scorciatoia dei sette tocchi dopo la prima configurazione.
- Primo provisioning locale: frase segreta (minimo 12 caratteri), identificativo e password (minimo 12 caratteri).
- Dopo il provisioning, la frase digitata nella chat viene confrontata localmente e intercettata prima del salvataggio nella cronologia.
- Se la frase è corretta, viene mostrata una schermata generica di verifica credenziali.
- L'apertura di EFESTO e ORACLE richiede identificativo e password corretti.
- Le pagine riservate non sono presenti nella barra di navigazione ordinaria.
- La sessione riservata parte bloccata a ogni nuova istanza dell'app e può essere chiusa dal comando di blocco nella pagina EFESTO.

## Controlli statici eseguiti
- Frase intercettata prima dell'aggiunta alla cronologia chat: PASS.
- Credenziali richieste dopo la frase: PASS.
- Hash e salt delle credenziali memorizzati, non password in chiaro: PASS.
- Accesso alle pagine riservate protetto dallo stato di sessione: PASS.
- Nessuna etichetta COSMO nella UI ordinaria: PASS.
- Nessuna voce riservata nella barra di navigazione ordinaria: PASS.

## Limiti e test non eseguiti
Questa è una protezione locale dimostrativa, non autenticazione di produzione. La frase e le credenziali vengono verificate sul dispositivo; il progetto non dispone ancora di un backend di identità, MFA, rate limiting, audit server-side o revoca sessioni. Non usare password reali o credenziali importanti in questa build.

Non è stato possibile eseguire Gradle, compilare l'APK o provarlo su emulatore/dispositivo in questo ambiente: Gradle e Android SDK non risultano installati/configurati. I controlli PASS sopra indicati sono controlli statici sul sorgente, non test runtime né test di compilazione.
