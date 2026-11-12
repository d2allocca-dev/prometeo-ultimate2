package it.prometeo.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EcosystemModulesTest {
    @Test fun oracleRanksRelevantMemories() {
        val entries = listOf(
            OracleEntry("1", "Viaggio", "Partenza per Roma", listOf("viaggio"), 1),
            OracleEntry("2", "Lavoro", "Riunione progetto", listOf("ufficio"), 2)
        )
        assertEquals("1", OracleMemory.search(entries, "Roma viaggio").first().id)
    }
    @Test fun deviceRegistryReplacesById() {
        val a = ConnectedDevice("x", "Lampada", DeviceCategory.LIGHT, "manual")
        val b = a.copy(name = "Lampada cucina")
        assertEquals(listOf(b), DeviceRegistry.upsert(listOf(a), b))
    }
    @Test fun flightPlanRequiresOfficialAirspaceCheck() {
        val plan = DroneFlightPlan("Test", "Roma", "2026-11-08T10:00:00", listOf(FlightChecklistItem("Batteria", true)))
        assertFalse(plan.readyForPilotReview())
        assertTrue(plan.copy(officialAirspaceCheckCompleted = true).readyForPilotReview())
    }
    @Test fun efestoNormalizesConfiguration() {
        assertEquals("Prometeo", EfestoConfig(assistantName = "  ").normalized().assistantName)
    }
}
