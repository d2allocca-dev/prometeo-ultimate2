package it.prometeo.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdaptiveRuntimeTest {
    @Test fun lowRamDeviceUsesSafeRulesOnlyProfile() {
        val plan = AdaptiveRuntime.plan(DeviceHardwareProfile(1024, 400, 4, "arm64-v8a", 35, true, false, true, false))
        assertEquals(LocalModelTier.RULES_ONLY, plan.tier)
        assertTrue(plan.memoryBudgetMb <= 96)
    }

    @Test fun standardDeviceGetsLargerConservativeBudget() {
        val plan = AdaptiveRuntime.plan(DeviceHardwareProfile(12288, 8192, 8, "arm64-v8a", 35, false, false, true, false))
        assertEquals(LocalModelTier.STANDARD, plan.tier)
        assertTrue(plan.workerThreads in 1..6)
        assertTrue(plan.maxContextTokens >= 4096)
    }

    @Test fun powerSaveModeLimitsWorkersAndBackgroundWork() {
        val plan = AdaptiveRuntime.plan(DeviceHardwareProfile(8192, 6000, 8, "arm64-v8a", 35, false, true, true, false))
        assertEquals(1, plan.workerThreads)
        assertTrue(plan.reduceBackgroundWork)
    }

    @Test fun missingRamNeverClaimsGenerativeTier() {
        val plan = AdaptiveRuntime.plan(DeviceHardwareProfile(null, null, 8, "unknown", 35, false, false, false, false))
        assertEquals(LocalModelTier.RULES_ONLY, plan.tier)
    }
}
