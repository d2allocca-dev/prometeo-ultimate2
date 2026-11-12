# PROMETEO ULTIMATE — prompt principale per Google AI Studio

Copia tutto questo prompt in Google AI Studio (Build/Chat) per proseguire lo sviluppo. Carica anche il progetto Android allegato e `RIFERIMENTO_DESIGN_PROMETEO_COSMO.png`.

---

Agisci come un team senior composto da Android/Kotlin Compose engineer, backend security engineer, UX designer e QA engineer. Devi continuare il progetto esistente **PROMETEO ULTIMATE**, senza riscriverlo inutilmente e senza eliminare le funzioni già presenti.

## Obiettivo
Realizzare un'app Android nativa Kotlin + Jetpack Compose, non una PWA. Mantieni PROMETEO ULTIMATE separata da PROMETEO EVOLUTION. In questa fase lavora esclusivamente su ULTIMATE.

## Design richiesto
Usa l'immagine di riferimento come direzione visiva, non come screenshot da copiare alla lettera:
- tema scuro blu notte, pannelli blu profondi, bordi luminosi ciano, accenti oro;
- home PROMETEO con intestazione, saluto, assistente/voce e riquadri funzionali;
- dashboard COSMO dopo autenticazione con stato autorizzato, pannelli EFESTO e ORACLE, sicurezza, dispositivi, analisi e impostazioni;
- tipografia leggibile, layout responsive per telefoni e tablet, accessibilità e contrasto adeguato;
- non mostrare alcun nome, voce, icona, link, route o riferimento a COSMO nell'interfaccia pubblica, notifiche, onboarding o menu ordinari.

## Flusso riservato
1. Al primo provisioning autorizzato, configurare una frase segreta e credenziali individuali.
2. In uso normale, la frase digitata nella chat deve essere riconosciuta prima di aggiungere il messaggio alla cronologia; non salvarla né inviarla al modello AI.
3. Dopo la frase, richiedere credenziali e MFA. Solo una risposta positiva del backend apre la dashboard riservata.
4. Il backend deve applicare ruoli e autorizzazioni: ZEUS è master supremo; ADE e Poseidone sono delegati revocabili solo da ZEUS; i delegati possono gestire amministratori ma non promuovere/rimuovere ZEUS; gli amministratori accedono solo se approvati esplicitamente da ZEUS.
5. EFESTO e ORACLE sono visibili solo nella sessione autorizzata. Logout, timeout, revoca, background prolungato o cambio account devono bloccare immediatamente l'area.
6. Non usare il solo stato locale, un PIN locale o la UI nascosta come controllo di sicurezza. La UI è solo presentazione; il server autorizza ogni API.

## Architettura e sicurezza obbligatorie
- Implementa backend autenticato con sessioni brevi, refresh rotation, MFA, rate limiting, audit log, revoca sessioni, protezione brute force e RBAC server-side.
- Password con Argon2id (o algoritmo password-hashing robusto supportato dal backend), salt univoco; mai password in chiaro.
- Frase segreta trattata come trigger secondario, mai come credenziale sufficiente. Non registrarla in log, analytics, crash report o cronologia.
- Segreti API solo in secret manager lato server. Mai incorporare chiavi Gemini o credenziali in APK, repository o file di configurazione distribuiti.
- Usa HTTPS/TLS; valida input; proteggi backup e dati locali; usa Android Keystore per eventuali token/device-bound keys.
- Prevedi audit delle azioni ZEUS/ADE/Poseidone/admin e approvazione ZEUS per ogni grant COSMO.
- Threat model, privacy policy e gestione cancellazione/esportazione dati.

## AI e Google AI Studio / Gemini
- Integra Gemini tramite backend proxy. L'app Android non deve contenere la Gemini API key.
- Se il backend non è configurato, dichiara chiaramente “modalità locale”; non simulare risposte AI remote.
- Gestisci timeout, retry limitati, rate limits, errori, streaming facoltativo, cancellazione richiesta e fallback offline.
- Non inviare frase segreta, password, token, dati di COSMO o memorie private al modello, salvo un'azione esplicita e autorizzata con minimizzazione dei dati.
- Fornisci istruzioni per configurare variabili segrete in AI Studio/Cloud Run o ambiente server, mai nel client.

## Funzioni già previste
- EFESTO: configurazione e personalizzazione dell'assistente.
- ORACLE: memoria persistente ricercabile, con cifratura, consenso e controlli di accesso.
- In futuro: eWeLink/Sonoff, Google Home/Alexa, dispositivi, PC, DJI Neo 2 e dati D-Flight. Non dichiarare integrazioni funzionanti finché API, autorizzazioni e test reali non sono completati. Per voli usare fonti aeronautiche ufficiali e controlli normativi aggiornati.
- Modalità offline limitata alle funzioni realmente disponibili localmente.

## Requisiti di consegna
1. Prima analizza tutto il progetto esistente e produci un inventario: file mantenuti, modificati, eliminati e motivazione.
2. Non inventare test superati. Esegui build e test realmente possibili e allega log.
3. Fornisci backend completo, schema database/migrazioni, API OpenAPI, test unitari/integration/security, istruzioni deploy e `.env.example` senza segreti.
4. Fornisci Android Studio project completo, Gradle wrapper, test Compose/UI, APK debug e release solo se realmente compilate e firmate correttamente.
5. Per ogni test indica PASS/FAIL/BLOCKED e ambiente/versioni. Verifica su emulatore e almeno un dispositivo reale prima di dichiarare release.
6. Non eliminare il flusso iniziale “Dai vita all'assistente”.
7. Non iniziare PROMETEO EVOLUTION: resta in attesa finché ULTIMATE non è completata e approvata.

## Stato noto da non travisare
La base fornita è una build di sviluppo. Le funzioni di memoria e configurazione esistenti sono locali; il collegamento AI e le integrazioni esterne non sono ancora reali. Il progetto non contiene ancora un backend di identità né una release Android verificata. Non dichiarare che queste lacune sono risolte finché non lo sono davvero.

Inizia con audit tecnico e piano di implementazione incrementale. Poi modifica il codice mantenendo le compatibilità esistenti. Chiedi chiarimenti solo se indispensabili; non inventare credenziali, account, endpoint o autorizzazioni.
