package com.toolsandtaverns.paleolithicera.world

import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.block.Blocks.WATER
import net.minecraft.fluid.Fluids
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.VegetationPlacedFeatures
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier
import net.minecraft.world.gen.placementmodifier.HeightmapPlacementModifier
import net.minecraft.world.gen.placementmodifier.NoiseThresholdCountPlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier
import net.minecraft.world.gen.placementmodifier.SurfaceWaterDepthFilterPlacementModifier
import org.spongepowered.asm.mixin.Mutable

object ModPlacedFeatures {

    val ELDERBERRY_BUSH_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("elderberry_bush_placed"))
    val CHAMOMILE_BUSH_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("chamomile_bush_placed"))
    val YARROW_PLANT_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("yarrow_plant_placed"))
    val WILD_GARLIC_PLANT_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("wild_garlic_plant_placed"))
    val EPHEDRA_PLANT_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("ephedra_plant_placed"))
    val SAGEBRUSH_PLANT_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("sagebush_plant_placed"))
    val WILD_MINT_PLANT_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("wild_mint_plant_placed"))
    val WILD_GINGER_PLANT_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("wild_ginger_plant_placed"))

    val WILLOW_PLACED: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("willow_placed"))

    fun bootstrap(context: Registerable<PlacedFeature>) {
        val configuredLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE)
        val elderberryBushConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.ELDERBERRY_BUSH_CONFIGURED_KEY)
        val chamomilePlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.CHAMOMILE_PLANT_CONFIGURED_KEY)
        val yarrowPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.YARROW_PLANT_CONFIGURED_KEY)
        val wildGarlicPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.WILD_GARLIC_PLANT_CONFIGURED_KEY)
        val ephedraPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.EPHEDRA_PLANT_CONFIGURED_KEY)
        val sagebrushPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.SAGEBRUSH_PLANT_CONFIGURED_KEY)
        val wildMintPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.WILD_MINT_PLANT_CONFIGURED_KEY)
        val wildGingerPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.WILD_GARLIC_PLANT_CONFIGURED_KEY)

        context.register(
            ELDERBERRY_BUSH_PLACED,
            PlacedFeature(
                elderberryBushConfigured,
                getHerbPlantPlacementModifiers()
            )
        )
        context.register(
            CHAMOMILE_BUSH_PLACED,
            PlacedFeature(
                chamomilePlantConfigured,
                getHerbPlantPlacementModifiers(42)
            )
        )
        context.register(
            YARROW_PLANT_PLACED,
            PlacedFeature(
                yarrowPlantConfigured,
                getHerbPlantPlacementModifiers(50)
            )
        )
        context.register(
            WILD_GARLIC_PLANT_PLACED,
            PlacedFeature(
                wildGarlicPlantConfigured,
                getHerbPlantPlacementModifiers(46)
            )
        )
        context.register(
            EPHEDRA_PLANT_PLACED,
            PlacedFeature(
                ephedraPlantConfigured,
                getHerbPlantPlacementModifiers(42)
            )
        )
        context.register(
            SAGEBRUSH_PLANT_PLACED,
            PlacedFeature(
                sagebrushPlantConfigured,
                getHerbPlantPlacementModifiers(50)
            )
        )
        context.register(
            WILD_MINT_PLANT_PLACED,
            PlacedFeature(
                wildMintPlantConfigured,
                getHerbPlantPlacementModifiers(42)
            )
        )
        context.register(
            WILD_GINGER_PLANT_PLACED,
            PlacedFeature(
                wildGingerPlantConfigured,
                getHerbPlantPlacementModifiers(40)
            )
        )

        context.register(
            WILLOW_PLACED,
            PlacedFeature(
                configuredLookup.getOrThrow(ModConfiguredFeatures.WILLOW_CONFIGURED_KEY),
                listOf(
                    NoiseThresholdCountPlacementModifier.of(-0.6, 0, 1),
                    RarityFilterPlacementModifier.of(80),
                    SquarePlacementModifier.of(),
                    HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES),
                    BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(ModBlocks.WILLOW_SAPLING.defaultState, Vec3i.ZERO)),
                    BiomePlacementModifier.of()
                ).toMutableList()
            )
        )



    }

    private fun getHerbPlantPlacementModifiers(rarity: Int = 40): List<PlacementModifier> {
        return listOf(
            NoiseThresholdCountPlacementModifier.of(-0.5, 0, 1),
            RarityFilterPlacementModifier.of(rarity),
            PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
            BiomePlacementModifier.of()
        )

    }
}