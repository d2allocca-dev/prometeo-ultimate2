package it.prometeo.ultimate

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import it.prometeo.core.AdaptiveExecutionPlan
import it.prometeo.core.AdaptiveRuntime
import it.prometeo.core.DeviceHardwareProfile
import java.util.Locale

/** Reads device capabilities without requesting sensitive permissions or sending telemetry. */
object AndroidHardwareProbe {
    fun inspect(context: Context): Pair<DeviceHardwareProfile, AdaptiveExecutionPlan> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memory = ActivityManager.MemoryInfo().also(activityManager::getMemoryInfo)
        val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val totalMb = if (Build.VERSION.SDK_INT >=  memoryInfoApi) (memory.totalMem / mb).toInt().coerceAtLeast(256) else null
        val availableMb = (memory.availMem / mb).toInt().coerceAtLeast(128)
        val profile = DeviceHardwareProfile(
            totalRamMb = totalMb,
            availableRamMb = availableMb,
            cpuCores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1),
            abi = Build.SUPPORTED_ABIS.firstOrNull()?.lowercase(Locale.ROOT) ?: "unknown",
            androidApi = Build.VERSION.SDK_INT,
            lowRamDevice = activityManager.isLowRamDevice || memory.lowMemory,
            powerSaveMode = power.isPowerSaveMode,
            hasNnapi = Build.VERSION.SDK_INT >= 27,
            hasGpuCompute = false
        )
        return profile to AdaptiveRuntime.plan(profile)
    }

    fun summary(profile: DeviceHardwareProfile, plan: AdaptiveExecutionPlan): String = buildString {
        append("Hardware rilevato: ")
        append(profile.totalRamMb?.let { "$it MB RAM" } ?: "RAM totale non disponibile")
        append(" · ${profile.cpuCores} core · ${profile.abi} · Android API ${profile.androidApi}. ")
        append("Profilo adattivo: ${plan.tier.name}. Budget locale stimato ${plan.memoryBudgetMb} MB; ${plan.workerThreads} thread. ")
        append(plan.explanation)
        if (plan.useAccelerator) append(" API acceleratore rilevata (disponibilità effettiva da verificare dal runtime).")
    }

    private const val mb = 1024L * 1024L
    private const val memoryInfoApi = 16
}
