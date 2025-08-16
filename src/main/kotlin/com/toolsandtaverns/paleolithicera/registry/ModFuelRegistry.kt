package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import net.fabricmc.fabric.api.registry.FuelRegistryEvents
import net.minecraft.item.FuelRegistry

object ModFuelRegistry {
    fun registerFuels() {
        FuelRegistryEvents.BUILD.register(FuelRegistryEvents.BuildCallback { builder: FuelRegistry.Builder, context: FuelRegistryEvents.Context ->
            builder.add(ModItems.BARK, 100)
            builder.add(ModItems.EDIBLE_PLANTS[EdiblePlants.WILLOW_BARK], 100)
        })
    }
}
