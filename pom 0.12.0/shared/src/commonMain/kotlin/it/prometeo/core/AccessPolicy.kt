package it.prometeo.core

/** Client-side policy for presentation only. A trusted backend MUST repeat every check. */
enum class Role { ZEUS, ADE, POSEIDONE, ADMIN, USER }
enum class Permission { VIEW_STANDARD, MANAGE_USERS, MANAGE_ADMINS, MANAGE_DELEGATES, CONFIGURE_SYSTEM, ACCESS_COSMO }

object AccessPolicy {
    fun allows(role: Role, permission: Permission, approvedByZeus: Boolean = false): Boolean = when (permission) {
        Permission.VIEW_STANDARD -> true
        Permission.MANAGE_USERS -> role in setOf(Role.ADMIN, Role.ADE, Role.POSEIDONE, Role.ZEUS)
        Permission.MANAGE_ADMINS -> role in setOf(Role.ADE, Role.POSEIDONE, Role.ZEUS)
        Permission.MANAGE_DELEGATES, Permission.CONFIGURE_SYSTEM -> role == Role.ZEUS
        Permission.ACCESS_COSMO -> role == Role.ZEUS || (role == Role.ADMIN && approvedByZeus)
    }

    fun canRevokeDelegate(actor: Role): Boolean = actor == Role.ZEUS
    fun canRemoveZeus(actor: Role): Boolean = false
    fun canPromoteToMaster(actor: Role): Boolean = actor == Role.ZEUS
}

data class LocalProfile(val displayName: String = "Prometeo", val role: Role = Role.USER)
