# Changelog

## 0.4.0-dev
- Aggiunto adapter di persistenza locale Android/desktop.
- Salvati configurazione iniziale, attivazione, nomi, preferenza offline, note, attività e cronologia chat.
- Aggiunta specifica architetturale e contratti backend per AI, memoria, ruoli e configurazioni.
- Test e compilazione APK non eseguiti, come richiesto.

## 0.4.1-alpha – 8 novembre 2026
- Corretto il codec locale per preservare correttamente backslash e separatori nei dati.
- Salvato l'endpoint AI tra le preferenze locali.
- Rimosso l'indicatore che permetteva di dichiarare un endpoint semplicemente “configurato”: ora viene indicato soltanto il formato HTTPS inserito, non una connessione verificata.
- Corretto il ripristino della procedura iniziale affinché azzeri anche lo stato di attivazione dell'assistente.
- Aggiornata la descrizione della persistenza delle note.

## 0.6.0
- Motore locale deterministico e ricerca appunti collegati alla chat.
- Test unitari comuni aggiunti; validazione endpoint HTTPS centralizzata.

## 0.7.0
- Aggiunti modelli EFESTO, ORACLE, registro dispositivi e piano volo/checklist.
- Rafforzata la validazione dell'endpoint HTTPS (rifiuta authority con userinfo e host locali).
- Aggiunti test unitari comuni per i nuovi moduli.
- Aggiornata documentazione con limiti espliciti delle integrazioni esterne e della sicurezza client.

## 0.7.1-dev
- Rafforzata la validazione sintattica degli endpoint HTTPS.
- Rifiutati user-info, query/fragment, porte non valide e host locali/privati.
- Aggiunti test unitari Kotlin per gli endpoint.
- Aggiornati metadati Android alla versione 0.7.1-dev.

## 0.8.0 – area riservata locale
- Aggiunto accesso non pubblicizzato tramite sette tocchi sul titolo superiore.
- Credenziale locale derivata PBKDF2-HMAC-SHA256 con salt casuale.
- Aggiunti pannelli EFESTO e ORACLE accessibili solo dopo sblocco locale.
- Rimosse le etichette ORACLE dalla navigazione standard.
- Aggiunte istruzioni e limiti di sicurezza in docs/IMPLEMENTAZIONE_AREA_RISERVATA_0_8.md.

## 0.10.1-dev
- Ridisegnata la Home PROMETEO per avvicinarla al concept grafico PROMETEO/COSMO: testata blu notte, saluto, invito vocale, sei riquadri funzionali e spazio personale.
- Aggiunta guida Windows per generare un APK debug con Android Studio o Gradle.
- Nessuna integrazione esterna viene dichiarata attiva se non collegata.


## 0.10.2 - Build pipeline hardening
- Declared the Kotlin JVM plugin version explicitly for the desktop module.
- Updated Android versionName to 0.10.2-dev.
- Hardened GitHub Actions with Gradle configuration check, shared tests, APK existence check and clearer artifact naming.
- This source package has not yet been compiled on a real Android SDK runner; a successful Actions run is still required to verify the APK.
