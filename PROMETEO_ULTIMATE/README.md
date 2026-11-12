# PROMETEO ULTIMATE 0.11.0-dev

Applicazione **Android nativa** in Kotlin / Jetpack Compose, con progetto Gradle multi-modulo. Questa consegna è il pacchetto sorgente pronto per essere aperto in Android Studio o compilato tramite GitHub Actions; non contiene un APK già compilato.

## Contenuto
- Dashboard Android nativa e assistente locale dimostrativo.
- Note, attività, preferenze e memoria ORACLE locale.
- Area COSMO riservata lato client, con schermata di configurazione dell'ecosistema.
- Email amministrativa preimpostata: `promrtro_ai-cloud@gmail.com` (modificabile in COSMO).
- Campi per endpoint cloud centrale, registrazione unità, DJI/D-Flight e domotica.
- Configurazione EFESTO e preferenze dell'assistente.
- Workflow GitHub Actions per compilare e pubblicare l'APK debug come artifact.

## Avvio / compilazione
Requisiti: JDK 17, Android SDK 35 e Gradle 8.9. Aprire la cartella del progetto in Android Studio e attendere la sincronizzazione Gradle. In alternativa, caricare il progetto in un repository GitHub e avviare il workflow `Build PROMETEO ULTIMATE APK` da Actions. L'artefatto viene pubblicato al termine della build.

## Configurazione proprietario
Leggere `docs/CONFIGURAZIONE_PROPRIETARIO.md` prima di usare COSMO. I campi configurabili sono predisposti ma un endpoint inserito non equivale a un servizio collegato.

## Limiti e sicurezza da non ignorare
- La schermata di accesso COSMO attuale è un blocco locale di sviluppo, non autenticazione di produzione. Non distribuire a utenti reali finché non viene sostituita da identità server-side, MFA e autorizzazione RBAC/ABAC sul backend.
- L'email indicata è un identificativo/recapito amministrativo, **non** un canale di comando globale. La distribuzione di comandi a unità nel mondo richiede backend autenticato, enrollment, certificati per dispositivo, comandi firmati, revoca, audit e protezioni anti-replay.
- **Non inserire la password Gmail nell'APK o nel repository.** Per l'accesso al servizio email si dovrà usare OAuth 2.0 oppure una credenziale applicativa custodita nel secret manager del backend.
- AI remota, DJI/D-Flight, eWeLink/Sonoff, Google Home e Alexa non sono dichiarati operativi finché non vengono collegati e testati con account, API e autorizzazioni ufficiali.
- Le funzioni locali non implicano disponibilità offline dell'AI generativa o dei dati live.

## Verifica
Consultare `docs/VERIFICA_0_11_0.md`. In questo ambiente non è stato possibile eseguire la build Android perché Android SDK e Gradle non sono installati. Non dichiarare l'APK pronto finché GitHub Actions/Android Studio non termina con esito positivo e l'APK non viene installato e testato su un dispositivo reale.
