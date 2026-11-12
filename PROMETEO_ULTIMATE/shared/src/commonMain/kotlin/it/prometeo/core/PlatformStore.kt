package it.prometeo.core

/** Persistenza locale per preferenze e dati non sensibili. I segreti non devono essere salvati qui. */
expect object PlatformStore {
    fun get(key: String): String?
    fun put(key: String, value: String)
    fun remove(key: String)
}

internal fun pack(items: List<String>): String = items.joinToString("\u001e") { item ->
    buildString {
        item.forEach { ch ->
            when (ch) {
                '\\' -> append("\\\\")
                '\u001e' -> append("\\e")
                else -> append(ch)
            }
        }
    }
}

internal fun unpack(value: String?): List<String> {
    if (value.isNullOrEmpty()) return emptyList()
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var escaped = false
    value.forEach { ch ->
        if (escaped) {
            when (ch) {
                'e' -> current.append('\u001e')
                '\\' -> current.append('\\')
                else -> { current.append('\\'); current.append(ch) }
            }
            escaped = false
        } else when (ch) {
            '\\' -> escaped = true
            '\u001e' -> { result.add(current.toString()); current.setLength(0) }
            else -> current.append(ch)
        }
    }
    if (escaped) current.append('\\')
    result.add(current.toString())
    return result
}


/** Derivazione locale PBKDF2 per il blocco UI. Non sostituisce autenticazione server. */
expect fun createSecretSalt(): String
expect fun deriveSecretHash(secret: String, salt: String): String
