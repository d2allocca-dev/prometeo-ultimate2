package it.prometeo.core

/** Local EFESTO configuration. Secrets and credentials are intentionally excluded. */
data class EfestoConfig(
    val assistantName: String = "Prometeo",
    val language: String = "it",
    val offlinePreferred: Boolean = true,
    val voiceEnabled: Boolean = false,
    val memoryEnabled: Boolean = true
) {
    fun normalized(): EfestoConfig = copy(
        assistantName = assistantName.trim().take(32).ifBlank { "Prometeo" },
        language = language.lowercase().take(8).ifBlank { "it" }
    )
}

/** ORACLE local memory: searchable user-authored knowledge, not autonomous learning. */
data class OracleEntry(val id: String, val title: String, val body: String, val tags: List<String>, val createdAtEpochMs: Long)
object OracleMemory {
    fun search(entries: List<OracleEntry>, query: String): List<OracleEntry> {
        val terms = tokenize(query)
        if (terms.isEmpty()) return emptyList()
        return entries.map { entry ->
            val haystack = tokenize(listOf(entry.title, entry.body, entry.tags.joinToString(" ")).joinToString(" "))
            entry to terms.count { it in haystack }
        }.filter { it.second > 0 }.sortedWith(compareByDescending<Pair<OracleEntry, Int>> { it.second }.thenByDescending { it.first.createdAtEpochMs }).map { it.first }
    }
    private fun tokenize(s: String) = s.lowercase().split(Regex("[^\\p{L}\\p{N}]+")).filter { it.length > 2 }.toSet()
}

data class ConnectedDevice(val id: String, val name: String, val category: DeviceCategory, val integration: String, val enabled: Boolean = false)
enum class DeviceCategory { LIGHT, SWITCH, SENSOR, CAMERA, SPEAKER, DISPLAY, PRINTER, DRONE, OTHER }

/** Registry only. A record never implies that a physical device is paired or controllable. */
object DeviceRegistry {
    fun upsert(devices: List<ConnectedDevice>, device: ConnectedDevice): List<ConnectedDevice> =
        (devices.filterNot { it.id == device.id } + device).sortedBy { it.name.lowercase() }
    fun remove(devices: List<ConnectedDevice>, id: String): List<ConnectedDevice> = devices.filterNot { it.id == id }
}

data class FlightChecklistItem(val label: String, val completed: Boolean = false)
data class DroneFlightPlan(
    val name: String,
    val location: String,
    val plannedAtIso: String,
    val checklist: List<FlightChecklistItem>,
    val officialAirspaceCheckCompleted: Boolean = false
) {
    fun readyForPilotReview(): Boolean = name.isNotBlank() && location.isNotBlank() && plannedAtIso.isNotBlank() &&
        checklist.isNotEmpty() && checklist.all { it.completed } && officialAirspaceCheckCompleted
}

/** Client-side convenience only. Production authorization must be enforced by a trusted server. */
object AdminConsolePolicy {
    fun canEditOwnProfile(role: Role) = role != Role.USER || role == Role.USER
    fun canChangeAssistantSettings(role: Role) = role == Role.ZEUS
    fun canManageAdmins(role: Role) = role == Role.ZEUS || role == Role.ADE || role == Role.POSEIDONE
    fun canManageDelegates(role: Role) = role == Role.ZEUS
    fun canApproveCosmo(role: Role) = role == Role.ZEUS
}
