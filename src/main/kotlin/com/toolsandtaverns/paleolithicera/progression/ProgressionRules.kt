package com.toolsandtaverns.paleolithicera.progression

import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.world.GameRules

object ProgressionRules {

    init {
        LOGGER.info("ProgressionRules initialized!")
    }

    val CAMPFIRE_LIT: GameRules.Key<GameRules.BooleanRule> by lazy {
        GameRuleRegistry.register(
            "pe_campfire_lit",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(false)
        )
    }

    val KNAPPING_STATION_PLACED: GameRules.Key<GameRules.BooleanRule> by lazy {
        GameRuleRegistry.register(
            "pe_knapping_station_placed",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(false)
        )
    }
}

