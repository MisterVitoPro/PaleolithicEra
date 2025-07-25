package com.toolsandtaverns.paleolithicera.world

import com.toolsandtaverns.paleolithicera.Constants
import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.PaleolithicEra.id
import net.minecraft.block.Blocks
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier

object ModPlacedFeatures {

    val ELDERBERRY_BUSH_PLACED: RegistryKey<PlacedFeature>
            = RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("elderberry_bush_placed"))

    fun bootstrap(context: Registerable<PlacedFeature>) {
        val configuredLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE)
        val bushConfigured = configuredLookup.getOrThrow(ModConfiguredFeatures.ELDERBERRY_BUSH_CONFIGURED_KEY)

        context.register(
            ELDERBERRY_BUSH_PLACED,
            PlacedFeature(
                bushConfigured,
                listOf(
                    RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()
                )
            )
        )
    }
}