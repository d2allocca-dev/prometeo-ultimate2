# Architettura e sicurezza — PROMETEO ULTIMATE

## Principi
1. ZEUS è il proprietario radice. Nessun altro ruolo può rimuoverlo o promuoversi a ZEUS.
2. ADE e POSEIDONE sono delegati revocabili esclusivamente da ZEUS.
3. I delegati possono amministrare utenti/amministratori entro limiti assegnati, ma non modificano la gerarchia radice.
4. COSMO resta riservato a ZEUS; un'eccezione per ADMIN richiede approvazione esplicita di ZEUS, registrata e revocabile.
5. Ogni controllo di sicurezza deve essere verificato lato server. Il client Android/Windows è considerato non attendibile.

## Architettura obiettivo
- Client condiviso Kotlin Multiplatform (Android + Windows Desktop).
- Identity service: account, passkey/MFA, sessioni brevi, recupero account.
- Authorization service: RBAC + attributi/ambito di delega, deny-by-default.
- Config service: configurazioni versionate, firmate, con verifica firma e rollback.
- Audit service: eventi append-only, esportazione e allarmi.
- Storage locale: cifratura con chiavi custodite da Android Keystore / Windows DPAPI o CNG.
- Sync: coda locale, conflitti espliciti, revoca e scadenza delle autorizzazioni offline.

## Regole di delega da implementare lato server
- `ZEUS`: gestione completa, nomina/revoca ADE e POSEIDONE, approvazione eccezioni COSMO.
- `ADE`, `POSEIDONE`: gestione amministratori nel proprio ambito; mai gestione ZEUS o delegati pari/superiori salvo permesso specifico esplicitamente assegnato da ZEUS.
- `ADMIN`: operazioni operative assegnate; nessuna gestione dei master.
- `USER`: funzioni personali autorizzate.

## Fasi successive
A. Identity + provisioning iniziale ZEUS in modo sicuro, senza password predefinite.
B. Backend RBAC e test automatici negativi (es. ADE non può revocare ZEUS).
C. Offline cache cifrata con scadenze, revoche e conflitti.
D. Configurazioni firmate e canale di aggiornamento.
E. Integrazioni singole, tramite API ufficiali e permessi espliciti.
F. Hardening, threat modeling, penetration test e beta limitata 20–50 persone.
