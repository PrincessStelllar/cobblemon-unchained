package us.timinc.mc.cobblemon.unchained.config

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.api.CounterTypeRegistry
import us.timinc.mc.cobblemon.counter.api.ScoreTypeRegistry
import us.timinc.mc.cobblemon.counter.extension.getCounterManager
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

abstract class AbstractBoosterConfig(
    @Transient
    val key: String,
    val defaultValue: Float = 0F,
) {
    val debug: Boolean = false
    val enabled: Boolean = true
    val lockToPlayer: Boolean = false
    val blacklist = mutableSetOf<PokemonMatcher>()
    val whitelist = mutableSetOf<PokemonMatcher>()
    val careAboutForms: Boolean = true
    val notifyPlayer: Boolean = true

    abstract val points: Map<String, Map<String, Float>>
    abstract val thresholds: Map<Int, Float>
    abstract val breakStreakOnSuccess: Set<String>

    fun getPointsFromThreshold(
        player: ServerPlayer,
        species: ResourceLocation,
        form: String,
    ): Float {
        val calcForm = if (careAboutForms) form else null
        val counterManager = player.getCounterManager()

        val totalPoints = points.entries.fold(0F) { pointAcc, (counterTypeName, scoreTypeMap) ->
            pointAcc + scoreTypeMap.entries.fold(0F) { counterTypeAcc, (scoreTypeName, multiplier) ->
                counterTypeAcc + (try {
                    val scoreType = ScoreTypeRegistry.findByType(scoreTypeName)
                    val counterType = CounterTypeRegistry.findByType(counterTypeName)

                    scoreType.getScore(counterManager, counterType, species, calcForm) * multiplier
                } catch (error: Error) {
                    0F
                })
            }
        }

        return thresholds.maxOfOrNull { if (it.key <= totalPoints) it.value else defaultValue } ?: defaultValue
    }
}