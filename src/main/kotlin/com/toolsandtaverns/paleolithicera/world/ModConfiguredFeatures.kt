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

    fun bootstrap(context: Registerable<ConfiguredFeature<*, *>>) {
        val bushBlockState = ModBlocks.ELDERBERRY_BUSH.defaultState.with(SweetBerryBushBlock.AGE, 3)

        register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            ELDERBERRY_BUSH_CONFIGURED_KEY,
            Feature.RANDOM_PATCH,
            ConfiguredFeatures.createRandomPatchFeatureConfig(
                Feature.SIMPLE_BLOCK,
                SimpleBlockFeatureConfig(BlockStateProvider.of(bushBlockState)),
                listOf(Blocks.GRASS_BLOCK)
            )
        )
    }

    fun registerKey(name: String): RegistryKey<ConfiguredFeature<*, *>> {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, id(name))
    }

    private fun <FC : FeatureConfig, F : Feature<FC>> register(
        context: Registerable<ConfiguredFeature<*, *>>,
        key: RegistryKey<ConfiguredFeature<*, *>>,
        feature: F,
        configuration: FC
    ) {
        context.register(key, ConfiguredFeature<FC, F>(feature, configuration))
    }
}