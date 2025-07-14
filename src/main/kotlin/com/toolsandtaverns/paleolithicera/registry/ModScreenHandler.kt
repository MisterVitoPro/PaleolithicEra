package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.screen.KnappingScreenHandler
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object ModScreenHandlers {

    lateinit var KNAPPING: ScreenHandlerType<KnappingScreenHandler>
        private set

    fun initialize() {
        KNAPPING = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "knapping"),
            ScreenHandlerType(::KnappingScreenHandler, FeatureSet.empty())
        )
    }
}
