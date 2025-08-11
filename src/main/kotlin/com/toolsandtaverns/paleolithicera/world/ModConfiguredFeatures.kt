package com.toolsandtaverns.paleolithicera.world

import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.block.Blocks
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.feature.*
import net.minecraft.world.gen.stateprovider.BlockStateProvider

object ModConfiguredFeatures {

    val ELDERBERRY_BUSH_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("elderberry_bush")
    val YARROW_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("yarrow_plant")

    fun bootstrap(context: Registerable<ConfiguredFeature<*, *>>) {
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            ELDERBERRY_BUSH_CONFIGURED_KEY,
            ConfiguredFeatures.createRandomPatchFeatureConfig(
                Feature.SIMPLE_BLOCK,
                SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.ELDERBERRY_BUSH.defaultState.with(SweetBerryBushBlock.AGE, 3))),
                listOf(Blocks.GRASS_BLOCK)
            )
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            YARROW_PLANT_CONFIGURED_KEY,
            ConfiguredFeatures.createRandomPatchFeatureConfig(
                Feature.SIMPLE_BLOCK,
                SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.YARROW_PLANT.defaultState.with(SweetBerryBushBlock.AGE, 3))),
                listOf(Blocks.GRASS_BLOCK)
            )
        )
    }

    fun registerKey(name: String): RegistryKey<ConfiguredFeature<*, *>> {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, id(name))
    }

    private fun <FC : FeatureConfig, F : Feature<FC>> F.register(
        context: Registerable<ConfiguredFeature<*, *>>,
        key: RegistryKey<ConfiguredFeature<*, *>>,
        configuration: FC
    ) {
        context.register(key, ConfiguredFeature<FC, F>(this, configuration))
    }
}