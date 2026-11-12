package it.prometeo.core

import android.content.Context

object AndroidStoreInit {
    lateinit var context: Context
    fun ready(): Boolean = ::context.isInitialized
}

actual object PlatformStore {
    private fun prefs() = AndroidStoreInit.context.getSharedPreferences("prometeo_local_v1", Context.MODE_PRIVATE)
    actual fun get(key: String): String? = if (contextReady) prefs().getString(key, null) else null
    actual fun put(key: String, value: String) { if (contextReady) prefs().edit().putString(key, value).apply() }
    actual fun remove(key: String) { if (contextReady) prefs().edit().remove(key).apply() }
    private val contextReady: Boolean get() = AndroidStoreInit.ready()
}


actual fun createSecretSalt(): String {
    val bytes = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) }
    return bytes.joinToString("") { "%02x".format(it) }
}

actual fun deriveSecretHash(secret: String, salt: String): String {
    val spec = javax.crypto.spec.PBEKeySpec(secret.toCharArray(), salt.chunked(2).map { it.toInt(16).toByte() }.toByteArray(), 180_000, 256)
    return try {
        javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded.joinToString("") { "%02x".format(it) }
    } finally { spec.clearPassword() }
}
