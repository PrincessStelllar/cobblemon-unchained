package us.timinc.mc.cobblemon.unchained.config

import us.timinc.mc.cobblemon.counter.CounterMod.CounterTypes.HATCH
import us.timinc.mc.cobblemon.counter.CounterMod.CounterTypes.KO
import us.timinc.mc.cobblemon.counter.CounterMod.CounterTypes.RESURRECTION
import us.timinc.mc.cobblemon.counter.CounterMod.ScoreTypes.STREAK

abstract class ShinyBoosterConfig(key: String) : AbstractBoosterConfig(key) {
    class Spawn : ShinyBoosterConfig("shiny.spawn") {
        override val points = mapOf(
            KO.type to mapOf(
                STREAK.type to 1F,
            ),
        )
        override val thresholds = mutableMapOf(
            100 to 1F,
            300 to 2F,
            500 to 3F,
        )
        override val breakStreakOnSuccess: Set<String> = setOf()
    }

    class Fish : ShinyBoosterConfig("shiny.fish") {
        override val points = mapOf(
            KO.type to mapOf(
                STREAK.type to 1F,
            ),
        )
        override val thresholds = mutableMapOf(
            75 to 1F,
            150 to 2F,
            300 to 3F,
        )
        override val breakStreakOnSuccess: Set<String> = setOf()
    }

    class Egg : ShinyBoosterConfig("shiny.egg") {
        override val points: Map<String, Map<String, Float>> = mapOf(
            HATCH.type to mapOf(
                STREAK.type to 1F,
            ),
        )
        override val thresholds: Map<Int, Float> = mapOf(
            75 to 1F,
            150 to 2F,
            300 to 3F,
        )
        override val breakStreakOnSuccess: Set<String> = setOf()
    }

    class Resurrection : ShinyBoosterConfig("shiny.resurrection") {
        override val points: Map<String, Map<String, Float>> = mapOf(
            RESURRECTION.type to mapOf(
                STREAK.type to 1F,
            ),
        )
        override val thresholds: Map<Int, Float> = mapOf(
            50 to 1F,
            100 to 2F,
            200 to 3F,
        )
        override val breakStreakOnSuccess: Set<String> = setOf()
    }

    class Capture : ShinyBoosterConfig("shiny.capture") {
        override val points: Map<String, Map<String, Float>> = mapOf()
        override val thresholds: Map<Int, Float> = mapOf()
        override val breakStreakOnSuccess: Set<String> = setOf()
    }

    class Snack : ShinyBoosterConfig("shiny.snack") {
        override val points = mapOf(
            KO.type to mapOf(
                STREAK.type to 1F,
            ),
        )
        override val thresholds = mutableMapOf(
            100 to 1F,
            300 to 2F,
            500 to 3F,
        )
        override val breakStreakOnSuccess: Set<String> = setOf()
    }
}