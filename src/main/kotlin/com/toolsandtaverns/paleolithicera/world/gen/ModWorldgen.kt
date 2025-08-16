package com.toolsandtaverns.paleolithicera.world.gen

import com.toolsandtaverns.paleolithicera.registry.ModEntityType
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
                BiomeKeys.FOREST,
                BiomeKeys.BIRCH_FOREST,
                BiomeKeys.DARK_FOREST,
                BiomeKeys.PALE_GARDEN,
                BiomeKeys.TAIGA,
                BiomeKeys.JUNGLE,
                BiomeKeys.RIVER,
                BiomeKeys.SPARSE_JUNGLE
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.ELDERBERRY_BUSH_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.PLAINS,
                BiomeKeys.SUNFLOWER_PLAINS,
                BiomeKeys.MEADOW,
                BiomeKeys.CHERRY_GROVE,
                BiomeKeys.TAIGA,
                BiomeKeys.GROVE,
                BiomeKeys.WINDSWEPT_HILLS,
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.CHAMOMILE_BUSH_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.PLAINS,
                BiomeKeys.SUNFLOWER_PLAINS,
                BiomeKeys.MEADOW,
                BiomeKeys.CHERRY_GROVE,
                BiomeKeys.GROVE,
                BiomeKeys.WINDSWEPT_HILLS,
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.YARROW_PLANT_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.FOREST,
                BiomeKeys.DARK_FOREST,
                BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                BiomeKeys.BIRCH_FOREST,
                BiomeKeys.FLOWER_FOREST,
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.WILD_GARLIC_PLANT_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.SNOWY_SLOPES,
                BiomeKeys.JAGGED_PEAKS,
                BiomeKeys.FROZEN_PEAKS,
                BiomeKeys.STONY_PEAKS,
                BiomeKeys.WINDSWEPT_HILLS
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.EPHEDRA_PLANT_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.BADLANDS,
                BiomeKeys.ERODED_BADLANDS,
                BiomeKeys.WOODED_BADLANDS,
                BiomeKeys.SAVANNA,
                BiomeKeys.WINDSWEPT_SAVANNA
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.SAGEBRUSH_PLANT_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.RIVER,
                BiomeKeys.MEADOW
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.WILD_MINT_PLANT_PLACED
        )

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.JUNGLE,
                BiomeKeys.SPARSE_JUNGLE,
                BiomeKeys.BAMBOO_JUNGLE,
                BiomeKeys.PLAINS,
                BiomeKeys.SAVANNA
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            ModPlacedFeatures.WILD_GINGER_PLANT_PLACED
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
            SpawnGroup.CREATURE, ModEntityType.BOAR_ENTITY, 40, 1, 2
        )
    }
}
