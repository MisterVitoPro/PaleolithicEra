package com.toolsandtaverns.paleolithicera.world

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier

object ModPlacedFeatures {

    val ELDERBERRY_BUSH_PLACED: RegistryKey<PlacedFeature>
            = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("elderberry_bush_placed"))

    val YARROW_PLANT_PLACED: RegistryKey<PlacedFeature>
            = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("yarrow_plant_placed"))

    fun bootstrap(context: Registerable<PlacedFeature>) {
        val configuredLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE)
        val elderberryBushConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.ELDERBERRY_BUSH_CONFIGURED_KEY)
        val yarrowPlantConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.YARROW_PLANT_CONFIGURED_KEY)

        context.register(
            ELDERBERRY_BUSH_PLACED,
            PlacedFeature(
                elderberryBushConfigured,
                listOf(
                    RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()
                )
            )
        )

        context.register(
            YARROW_PLANT_PLACED,
            PlacedFeature(
                yarrowPlantConfigured,
                listOf(
                    RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()
                )
            )
        )
    }
}