package it.prometeo.core

/** Security boundary documentation: local roles are never proof of identity.
 * Production release requires server-issued sessions, MFA, server-side RBAC,
 * audit logging, rate limits and revocation. No password or API key belongs in app storage.
 */
object SecurityRequirements {
    val productionRequirements = listOf(
        "TLS for every network connection",
        "Server-side identity verification and role authorization",
        "MFA for privileged accounts",
        "Argon2id password hashing on the server",
        "Short-lived sessions with revocation and rotation",
        "Rate limiting and security audit events",
        "Secrets stored only in a server-side secret manager"
    )
}
