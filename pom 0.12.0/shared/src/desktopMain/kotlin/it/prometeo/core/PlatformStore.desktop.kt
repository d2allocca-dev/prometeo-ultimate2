package it.prometeo.core

import java.util.prefs.Preferences

actual object PlatformStore {
    private val prefs = Preferences.userRoot().node("it/prometeo/ultimate")
    actual fun get(key: String): String? = prefs.get(key, null)
    actual fun put(key: String, value: String) { prefs.put(key, value) }
    actual fun remove(key: String) { prefs.remove(key) }
}


actual fun createSecretSalt(): String {
    val bytes = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) }
    return bytes.joinToString("") { "%02x".format(it) }
}
actual fun deriveSecretHash(secret: String, salt: String): String {
    val spec = javax.crypto.spec.PBEKeySpec(secret.toCharArray(), salt.chunked(2).map { it.toInt(16).toByte() }.toByteArray(), 180_000, 256)
    return try { javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded.joinToString("") { "%02x".format(it) } } finally { spec.clearPassword() }
}
