package com.toolsandtaverns.paleolithicera.world.gen

import com.toolsandtaverns.paleolithicera.world.ModPlacedFeatures
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.gen.GenerationStep

object ModTreeGeneration {

    fun initialize() {
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(BiomeKeys.FOREST, BiomeKeys.RIVER, BiomeKeys.SWAMP, BiomeKeys.MANGROVE_SWAMP),
            GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.WILLOW_PLACED
        )
    }

}