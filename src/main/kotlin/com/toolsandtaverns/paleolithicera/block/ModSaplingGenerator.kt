package com.toolsandtaverns.paleolithicera.block

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.world.ModConfiguredFeatures
import net.minecraft.block.SaplingGenerator
import net.minecraft.registry.RegistryKey
import net.minecraft.world.gen.feature.ConfiguredFeature
import java.util.*

object ModSaplingGenerator {
    val WILLOW: SaplingGenerator = SaplingGenerator(
        "$MOD_ID:willow_sapling",
        Optional.empty(),
        Optional.of(ModConfiguredFeatures.WILLOW_CONFIGURED_KEY),
        Optional.empty()
    )
}