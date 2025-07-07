package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import com.tootsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screen.ingame.HandledScreens

object PaleolithicEraClient : ClientModInitializer {
    override fun onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.KNAPPING, ::KnappingStationScreen)
    }
}
