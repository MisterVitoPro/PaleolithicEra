package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.screen.HideDryerScreenHandler
import com.toolsandtaverns.paleolithicera.screen.KnappingScreenHandler
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.BlockPos

object ModScreenHandlers {

    lateinit var KNAPPING: ScreenHandlerType<KnappingScreenHandler>
        private set
    lateinit var HIDE_DRYER: ScreenHandlerType<HideDryerScreenHandler>
        private set

    fun initialize() {
        KNAPPING = Registry.register(
            Registries.SCREEN_HANDLER,
            id("knapping"),
            ExtendedScreenHandlerType(
                { syncId, inventory, pos -> KnappingScreenHandler(syncId, inventory, pos) },
                BlockPos.PACKET_CODEC
            )
        )
        HIDE_DRYER = Registry.register(
            Registries.SCREEN_HANDLER,
            id("hide_dryer"),
            ExtendedScreenHandlerType(
                { syncId, inventory, pos -> HideDryerScreenHandler(syncId, inventory, pos) },
                BlockPos.PACKET_CODEC
            )
        )
    }
}
