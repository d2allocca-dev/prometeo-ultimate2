package it.prometeo.core

/** Hardware facts collected by the platform adapter. Unknown values stay explicit (null). */
data class DeviceHardwareProfile(
    val totalRamMb: Int?,
    val availableRamMb: Int?,
    val cpuCores: Int,
    val abi: String,
    val androidApi: Int,
    val lowRamDevice: Boolean,
    val powerSaveMode: Boolean,
    val hasNnapi: Boolean,
    val hasGpuCompute: Boolean
)

enum class LocalModelTier { RULES_ONLY, TINY, COMPACT, STANDARD }

/** A conservative resource plan. It never promises performance beyond measured device limits. */
data class AdaptiveExecutionPlan(
    val tier: LocalModelTier,
    val workerThreads: Int,
    val memoryBudgetMb: Int,
    val maxContextTokens: Int,
    val useAccelerator: Boolean,
    val reduceBackgroundWork: Boolean,
    val explanation: String
)

object AdaptiveRuntime {
    fun plan(profile: DeviceHardwareProfile): AdaptiveExecutionPlan {
        val cores = profile.cpuCores.coerceIn(1, 64)
        val total = profile.totalRamMb?.coerceAtLeast(256)
        val available = profile.availableRamMb?.coerceAtLeast(128)
        val usable = minOf(total ?: 1024, available ?: 512)
        val constrained = profile.lowRamDevice || profile.powerSaveMode || usable < 900
        val tier = when {
            constrained || total == null -> LocalModelTier.RULES_ONLY
            total >= 8192 && usable >= 4096 && cores >= 8 -> LocalModelTier.STANDARD
            total >= 4096 && usable >= 1800 && cores >= 4 -> LocalModelTier.COMPACT
            total >= 2048 && usable >= 900 -> LocalModelTier.TINY
            else -> LocalModelTier.RULES_ONLY
        }
        val memoryBudget = when (tier) {
            LocalModelTier.RULES_ONLY -> 96
            LocalModelTier.TINY -> minOf(384, usable / 4)
            LocalModelTier.COMPACT -> minOf(1024, usable / 3)
            LocalModelTier.STANDARD -> minOf(2048, usable / 3)
        }.coerceAtLeast(64)
        val threads = when {
            profile.powerSaveMode -> 1
            else -> (cores - 1).coerceIn(1, 6)
        }
        val context = when (tier) {
            LocalModelTier.RULES_ONLY -> 0
            LocalModelTier.TINY -> 1024
            LocalModelTier.COMPACT -> 2048
            LocalModelTier.STANDARD -> 4096
        }
        val accelerator = tier != LocalModelTier.RULES_ONLY && (profile.hasNnapi || profile.hasGpuCompute)
        val reason = when (tier) {
            LocalModelTier.RULES_ONLY -> "Risorse limitate o non rilevate: funzioni locali leggere; modello generativo non attivato."
            LocalModelTier.TINY -> "Profilo leggero: eventuale modello locale molto compatto, se installato e compatibile."
            LocalModelTier.COMPACT -> "Profilo compatto: modello locale quantizzato compatibile, se installato."
            LocalModelTier.STANDARD -> "Profilo standard: modello locale più capace, se installato e compatibile."
        }
        return AdaptiveExecutionPlan(tier, threads, memoryBudget, context, accelerator, constrained, reason)
    }
}
