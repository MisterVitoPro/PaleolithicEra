package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.util.id
import com.toolsandtaverns.paleolithicera.world.feature.SmallCaveFeature
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature

object ModFeatures {
    
    val SMALL_CAVE_FEATURE: Feature<DefaultFeatureConfig> = SmallCaveFeature(DefaultFeatureConfig.CODEC)
    
    fun initialize() {
        Registry.register(
            Registries.FEATURE,
            id("small_cave"),
            SMALL_CAVE_FEATURE
        )
    }
}