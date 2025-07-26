package com.toolsandtaverns.paleolithicera.world.gen

import com.toolsandtaverns.paleolithicera.world.ModPlacedFeatures
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.gen.GenerationStep

object ModWorldgen {

    fun initialize() {
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.PLAINS,
                BiomeKeys.FOREST,
                BiomeKeys.FLOWER_FOREST,
                BiomeKeys.BIRCH_FOREST,
                BiomeKeys.DARK_FOREST,
                BiomeKeys.PALE_GARDEN,
                BiomeKeys.TAIGA,
                BiomeKeys.JUNGLE,
                BiomeKeys.GROVE,
                BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.ELDERBERRY_BUSH_PLACED
        )
    }
}
