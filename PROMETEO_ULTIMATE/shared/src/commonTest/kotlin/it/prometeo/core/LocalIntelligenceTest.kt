package it.prometeo.core

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class LocalIntelligenceTest {
    @Test fun greetingsUseChosenAssistantName() {
        assertContains(LocalIntelligence.respond("Ciao", "Atena", emptyList(), emptyList()), "Atena")
    }
    @Test fun memorySearchReturnsRelevantNotes() {
        val result = LocalIntelligence.respond("cerca nelle note drone", "Prometeo", listOf("Pianificare volo drone sabato", "Comprare pane"), emptyList())
        assertContains(result, "drone")
    }
    @Test fun taskSummaryOnlyShowsOpenItems() {
        val result = LocalIntelligence.respond("elenca attività", "Prometeo", emptyList(), listOf("Aperta" to false, "Chiusa" to true))
        assertContains(result, "Aperta")
        kotlin.test.assertFalse(result.contains("Chiusa"))
    }
    @Test fun emptyInputDoesNotCrash() {
        assertContains(LocalIntelligence.respond("   ", "Prometeo", emptyList(), emptyList()), "Scrivi")
    }
    @Test fun endpointRejectsPlainHttp() {
        assertEquals(false, isSecureAiEndpoint("http://example.com"))
        assertEquals(true, isSecureAiEndpoint("https://example.com/api"))
    }
}
