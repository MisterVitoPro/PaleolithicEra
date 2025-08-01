package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.screen.KnappingScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object ModScreenHandlers {

    lateinit var KNAPPING: ScreenHandlerType<KnappingScreenHandler>
        private set

    fun initialize() {
        KNAPPING = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "knapping"),
            ExtendedScreenHandlerType(
                { syncId, inventory, pos -> KnappingScreenHandler(syncId, inventory, pos) },
                BlockPos.PACKET_CODEC
            )
        )
    }
}
