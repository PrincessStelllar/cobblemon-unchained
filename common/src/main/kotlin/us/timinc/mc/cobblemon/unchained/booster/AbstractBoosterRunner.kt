package us.timinc.mc.cobblemon.unchained.booster

import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.api.CounterTypeRegistry
import us.timinc.mc.cobblemon.counter.extension.getCounterManager
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonRepresentation
import us.timinc.mc.cobblemon.unchained.Unchained
import us.timinc.mc.cobblemon.unchained.config.AbstractBoosterConfig
import us.timinc.mc.cobblemon.unchained.event.BoostApplication

abstract class AbstractBoosterRunner<T : AbstractBoosterConfig>(
    val player: ServerPlayer,
    val pokemon: PokemonRepresentation<*>,
    val lockToPlayer: () -> Unit = {},
) {
    private val debugger = Unchained.debugger.getCaseDebugger()
    fun debug(msg: String, bypass: Boolean = false) = debugger.debug(msg, bypass || config.debug)

    abstract val config: T

    val species = pokemon.species!!
    val form = pokemon.form ?: species.standardForm
    val unlockedBoost
        get() = config.getPointsFromThreshold(player, species.resourceIdentifier, form.name)

    fun runThrough(): Boolean {
        if (!config.enabled) return false
        if (!LimitedList.PokemonMatcherList.matchesList(pokemon.getPokemon(), config.whitelist, config.blacklist)) {
            debug("${species.name}|${form.name} is prohibited by the whitelist/blacklist.")
            return false
        }

        val passedRoll = roll()
        val passedTest = test()

        var passedEvent = false
        val preEvent = BoostApplication.Pre(config.key, pokemon, player, passedRoll, passedTest)
        Unchained.Events.BOOST_APPLICATION_PRE.postThen(
            preEvent,
            {
                passedEvent = false
            }, {
                passedEvent = true
            }
        )

        if ((!passedTest && !preEvent.ignoreTest) || (!passedRoll && !preEvent.ignoreRoll) || !passedEvent) return false

        boost()
        config.breakStreakOnSuccess.forEach {
            player.getCounterManager().breakStreak(CounterTypeRegistry.findByType(it))
        }
        if (config.lockToPlayer) {
            lockToPlayer()
        }
        if (config.notifyPlayer) {
            notifyPlayer()
        }
        Unchained.Events.BOOST_APPLICATION_POST.post(
            BoostApplication.Post(
                config.key,
                pokemon,
                player,
            )
        )
        return true
    }

    abstract fun roll(): Boolean

    abstract fun test(): Boolean

    abstract fun boost()

    open fun notifyPlayer() {
        player.sendSystemMessage(Unchained.TranslationComponents.notify(config.key, species, form))
    }
}