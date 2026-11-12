# PROMETEO ULTIMATE 0.12.0-adaptive

Applicazione **Android nativa** in Kotlin / Jetpack Compose, con progetto Gradle multi-modulo. Questa consegna è il pacchetto sorgente pronto per essere aperto in Android Studio o compilato tramite GitHub Actions; non contiene un APK già compilato.

## Contenuto
- Dashboard Android nativa e assistente locale deterministico.
- Rilevamento hardware Android e selezione adattiva prudente del profilo risorse (RAM, core, ABI, risparmio energetico).
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
- Il nucleo locale attuale gestisce funzioni deterministiche, note e attività offline; non include ancora un modello generativo LLM locale.
- L'adattamento hardware seleziona budget e profili, ma non aumenta fisicamente le capacità del dispositivo e non installa modelli AI.
- Il profilo adattivo è stato sottoposto a smoke test Kotlin; la build Android completa e i test su hardware reale restano da eseguire.

## Verifica
Consultare `docs/STATO_RELEASE_0_12_0.md` e `docs/ADATTAMENTO_HARDWARE_E_OFFLINE.md`. In questo ambiente non è stato possibile eseguire la build Android perché Android SDK e Gradle non sono installati. Non dichiarare l'APK pronto finché GitHub Actions/Android Studio non termina con esito positivo e l'APK non viene installato e testato su un dispositivo reale.

---

## Pacchetto sorgente adattivo — pre-APK

This ZIP is a consolidated Android-capable source project prepared for upload to a NEW GitHub repository.
It is **not a finished production application** and no APK is included.

### Included
- PROMETEO ULTIMATE 0.12.0-adaptive Android/Kotlin Multiplatform source.
- Shared domain modules, local storage abstractions, adaptive hardware planner and tests.
- Existing Android application module and Gradle configuration.
- Root-level GitHub Actions workflow that installs Android SDK platform/build tools without requesting the obsolete `tools` package, runs shared tests, builds `assembleDebug`, and uploads the APK artifact.

### Still requiring implementation or external validation
- Il tema Zeus non è ancora integrato nelle schermate effettive.
- COSMO client-side role policy is not a secure server-side authorization system. Production COSMO needs a deployed backend, MFA, audit, revocation and security review.
- Il modello AI generativo offline, PROMETEO EVOLUTION, DJI/D-Flight, eWeLink/Sonoff, Google Home/Alexa, wearable e sincronizzazione multi-dispositivo non sono dimostrati come integrazioni operative.
- Third-party integrations require their supported APIs, developer accounts, credentials, device access and compliance checks.
- La build Gradle/Android e i test su dispositivi reali non sono stati eseguiti in questa sessione. GitHub Actions deve terminare con successo prima di considerare generato un APK.

### Build
Open this directory in Android Studio, or upload its contents to the root of a new GitHub repository. GitHub Actions can be started from Actions → “PROMETEO ULTIMATE - Android APK” → Run workflow.
Expected artifact name: `PROMETEO-ULTIMATE-debug-apk`.

### Security
Do not commit passwords, API keys, OAuth secrets, signing keys or personal credentials. Configure secrets only in a backend secret manager or GitHub Actions secrets when strictly needed. The client-side access policy is UI logic only and must never be treated as proof of identity.
