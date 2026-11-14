# Prometeo COSMO backend

This is a runnable API foundation for server-enforced identity, MFA, ZEUS/ADE/Poseidone delegation, admin approval/revocation, COSMO gating, configuration distribution, per-user ORACLE sync and audit events.

## Start
1. Copy `.env.example` to `.env`.
2. Set independent, high-entropy `JWT_SECRET` and one-time `ZEUS_BOOTSTRAP_TOKEN`.
3. Run `docker compose up --build`.
4. Call `POST /v1/bootstrap` once to create ZEUS, then enroll and approve accounts.
5. Configure TOTP MFA with `/v1/mfa/setup` and `/v1/mfa/confirm`.

Never expose this service directly to the internet without HTTPS termination, firewalling, backups, monitoring, rate limiting/WAF and secrets management. Replace SQLite with managed PostgreSQL for production by setting DATABASE_URL. The bootstrap token must be removed/rotated after first use. The API intentionally has no permissive CORS origins.

## Important boundaries
- Role checks are enforced by the API, not trusted from the Android client.
- ADE and Poseidone can approve/revoke ordinary admins but cannot appoint masters or revoke ZEUS/delegates.
- ZEUS alone can change system configuration and appoint/revoke delegates.
- COSMO is restricted to ZEUS and ZEUS-approved admins.
- Access tokens are short-lived and sessions are revocable; TOTP is required for login.
- ORACLE records are isolated per user; configuration sync is restricted to masters.
- This backend does not yet implement provider OAuth flows for Google Home/Alexa/eWeLink/Sonoff, a local LLM runtime, or an automatic Evolution pairing flow. No fake connected status is exposed.
