package com.toolsandtaverns.paleolithicera.registry

import net.fabricmc.fabric.api.registry.FuelRegistryEvents
import net.minecraft.item.FuelRegistry

object ModFuelRegistry {
    fun registerFuels() {
        FuelRegistryEvents.BUILD.register(FuelRegistryEvents.BuildCallback { builder: FuelRegistry.Builder, context: FuelRegistryEvents.Context ->
            builder.add(ModItems.BARK, 100)
        })
    }
}
