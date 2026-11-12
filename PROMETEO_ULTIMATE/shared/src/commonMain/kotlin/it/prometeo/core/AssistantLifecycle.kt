package it.prometeo.core

/** First-run lifecycle. These flags are UI state, never proof of identity or authorization. */
data class AssistantLifecycle(val configured: Boolean, val awakened: Boolean) {
    fun completeSetup(): AssistantLifecycle = copy(configured = true)
    fun awaken(): AssistantLifecycle = if (configured) copy(awakened = true) else this
    fun reset(): AssistantLifecycle = AssistantLifecycle(false, false)
}

/** Strict syntax check only; it does not prove that the endpoint exists or is trusted. */
fun isSecureAiEndpoint(value: String): Boolean {
    val candidate = value.trim()
    if (!candidate.startsWith("https://", ignoreCase = true)) return false
    if (candidate.any { it.isWhitespace() } || candidate.contains('@') || candidate.contains('\\')) return false
    if (candidate.contains('?') || candidate.contains('#')) return false // credentials must never be placed in URL query/fragment

    val remainder = candidate.substring(8)
    val authority = remainder.substringBefore('/')
    if (authority.isBlank() || authority.startsWith('.') || authority.endsWith('.')) return false
    if (authority.contains('[') || authority.contains(']')) return false // IPv6 literals are not accepted by this simple validator

    val host = authority.substringBefore(':')
    val portText = authority.substringAfter(':', "")
    if (authority.count { it == ':' } > 1) return false
    if (portText.isNotEmpty()) {
        val port = portText.toIntOrNull() ?: return false
        if (port !in 1..65535 || portText.any { !it.isDigit() }) return false
    }
    if (authority.endsWith(':')) return false

    if (host.equals("localhost", true) || host.endsWith(".localhost", true) || host.endsWith(".local", true)) return false
    val labels = host.split('.')
    if (labels.size < 2 || labels.any { label ->
        label.isEmpty() || label.length > 63 || label.startsWith('-') || label.endsWith('-') ||
            label.any { !(it.isLetterOrDigit() || it == '-') }
    }) return false
    if (host.length > 253 || labels.last().length < 2) return false

    // Reject private/loopback IPv4 destinations in this client-side convenience validator.
    val ipv4 = host.split('.')
    if (ipv4.size == 4 && ipv4.all { it.toIntOrNull() in 0..255 }) {
        val octets = ipv4.map { it.toInt() }
        if (octets[0] == 10 || octets[0] == 127 || octets[0] == 0 ||
            (octets[0] == 192 && octets[1] == 168) ||
            (octets[0] == 172 && octets[1] in 16..31) ||
            (octets[0] == 169 && octets[1] == 254)) return false
    }
    return true
}
