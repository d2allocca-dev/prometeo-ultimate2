package it.prometeo.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EndpointValidationTest {
    @Test fun acceptsHttpsHostAndValidPort() {
        assertTrue(isSecureAiEndpoint("https://api.example.com/v1/chat"))
        assertTrue(isSecureAiEndpoint("https://api.example.com:8443/v1/chat"))
    }
    @Test fun rejectsInsecureOrMalformedUrls() {
        listOf(
            "http://api.example.com", "https://", "https://bad host.example",
            "https://example.com:99999", "https://example.com:abc", "https://example.com:",
            "https://user@example.com", "https://example.com?api_key=secret",
            "https://example.com#token", "https://localhost/api", "https://printer.local/api",
            "https://192.168.1.20/api", "https://10.0.0.2/api", "https://-bad.example/api",
            "https://bad-.example/api", "https://example..com/api"
        ).forEach { assertFalse(isSecureAiEndpoint(it), "Should reject: $it") }
    }
}
