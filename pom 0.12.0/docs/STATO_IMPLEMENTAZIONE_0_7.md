# PROMETEO ULTIMATE – stato sorgenti 0.7.0

## Implementato nel codice condiviso
- Ciclo primo avvio limitato e attivazione esplicita dell'assistente.
- Preferenze e dati locali persistenti tramite PlatformStore.
- Assistente locale deterministico per consultazione note e attività (non è un modello generativo).
- Modello di configurazione EFESTO locale, con normalizzazione dei campi non segreti.
- API di ricerca ORACLE su memorie locali strutturate.
- Registro locale dei dispositivi: inventario e metadati, senza dichiarare pairing o controllo fisico.
- Modello di piano volo e checklist che richiede esplicitamente il controllo dello spazio aereo su fonte ufficiale.
- Regole client di presentazione ruoli; non costituiscono autenticazione o autorizzazione di sicurezza.
- Test unitari comuni aggiunti per memoria, inventario dispositivi, checklist e normalizzazione configurazione.

## Non implementabile in modo reale senza servizi, credenziali, dispositivi e decisioni del proprietario
- Un'AI generativa richiede la scelta e configurazione di un provider/modello e un backend con credenziali custodite lato server. Non inserire chiavi API nell'app.
- Senzienza non è una proprietà tecnica che il software possa garantire. L'app può essere adattiva e personalizzabile, senza dichiararsi cosciente.
- ORACLE qui è memoria locale ricercabile; sincronizzazione multi-dispositivo, cifratura end-to-end e memoria semantica remota richiedono backend e gestione chiavi.
- COSMO/ruoli sono solo policy di interfaccia nel client. La sicurezza effettiva richiede identity provider, MFA, backend RBAC, audit e revoca sessioni.
- Google Home/Alexa/Sonoff/eWeLink richiedono accesso alle rispettive API, account sviluppatore, OAuth e dispositivi compatibili. Il registro locale non controlla i dispositivi.
- DJI/D-Flight richiedono API/accordi e fonti aeronautiche ufficiali aggiornate; il piano volo è una checklist e non un'autorizzazione al volo.
- Funzioni offline generative richiedono un modello on-device, selezionato in base a RAM, storage e licenza del dispositivo.

## Verifiche
- Archivio ZIP verificato dopo la creazione.
- Test unitari aggiunti ma non eseguiti in questa sessione.
- Build Android/Gradle e test su dispositivo non eseguiti.
