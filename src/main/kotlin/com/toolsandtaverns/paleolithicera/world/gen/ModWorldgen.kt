package com.toolsandtaverns.paleolithicera.world.gen

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.world.ModPlacedFeatures
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.entity.SpawnGroup
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

        BiomeModifications.addSpawn(
            BiomeSelectors.includeByKey(
                BiomeKeys.BIRCH_FOREST,
                BiomeKeys.CHERRY_GROVE,
                BiomeKeys.DARK_FOREST,
                BiomeKeys.FLOWER_FOREST,
                BiomeKeys.FOREST,
                BiomeKeys.PLAINS,
                BiomeKeys.TAIGA,
                ),
            SpawnGroup.CREATURE, ModEntities.BOAR_ENTITY, 60, 1, 3)
    }
}
