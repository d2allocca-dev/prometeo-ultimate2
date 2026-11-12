# Backend COSMO — contratto di integrazione

Questa cartella documenta il punto di estensione per il backend centrale; non contiene un server di produzione. L'app Android non deve mai contenere credenziali condivise o password della casella email.

Endpoint previsti (da implementare e proteggere prima del rilascio):
- `POST /v1/session` — OIDC/passkey e MFA.
- `POST /v1/units/enroll` — registrazione unità tramite codice monouso.
- `POST /v1/assistant/respond` — richieste AI instradate dal server.
- `GET/PUT/DELETE /v1/oracle` — memoria con consenso, export e cancellazione.
- `GET/POST /v1/admin/users` — gestione utenti con policy server-side.
- `POST /v1/admin/delegates` — ZEUS only.
- `POST /v1/commands` — invio comandi firmati a unità autorizzate.
- `POST /v1/config/publish` — configurazioni firmate e versionate.

Tutti gli endpoint richiedono TLS, token brevi e revocabili, rate limiting, audit append-only, controlli anti-replay e segreti in un secret manager. La mail amministrativa è un'identità/recapito, non il trasporto dei comandi.
