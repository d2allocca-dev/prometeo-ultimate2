# PROMETEO ULTIMATE 0.7.1 – test preliminare e correzioni

## Difetto corretto
Il controllo precedente dell'endpoint HTTPS era permissivo: accettava porte malformate, host locali e URL con query/fragment. È stato sostituito con una validazione sintattica più rigorosa e sono stati aggiunti test unitari dedicati.

## Test eseguibili
- `python scripts/simulate_preliminary.py`: simulazione logica preesistente.
- `shared/src/commonTest/.../EndpointValidationTest.kt`: test Kotlin aggiuntivi per host, porte, user-info, query, fragment e destinazioni locali.
- Verifica XML del manifest e integrità ZIP.

## Limiti
La validazione URL è solo un controllo client-side: non autentica il server, non verifica certificati applicativi/pinning e non sostituisce un backend. Le policy ruoli nel client sono solo presentazione; autorizzazione COSMO e gestione ZEUS devono essere ripetute lato server. Non sono stati eseguiti Gradle, compilazione Android, emulatori o dispositivi in questo ambiente.
