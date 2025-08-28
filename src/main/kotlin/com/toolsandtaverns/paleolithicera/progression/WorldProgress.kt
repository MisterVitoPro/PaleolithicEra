package com.toolsandtaverns.paleolithicera.progression

import net.minecraft.server.world.ServerWorld

object WorldProgress {

    fun markCampfireLit(world: ServerWorld) {
        val rule = world.gameRules[ProgressionRules.CAMPFIRE_LIT]
        if (!rule.get()) rule.set(true, world.server)
    }

    fun markKnappingStationPlaced(world: ServerWorld) {
        val rule = world.gameRules[ProgressionRules.KNAPPING_STATION_PLACED]
        if (!rule.get()) rule.set(true, world.server)
    }

    fun isUnlocked(world: ServerWorld): Boolean {
        return world.gameRules.getBoolean(ProgressionRules.CAMPFIRE_LIT) &&
               world.gameRules.getBoolean(ProgressionRules.KNAPPING_STATION_PLACED)
    }
}

