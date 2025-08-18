package com.toolsandtaverns.paleolithicera.world

import com.google.common.collect.ImmutableList
import com.toolsandtaverns.paleolithicera.block.EdiblePlantBlock
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.util.id
import com.toolsandtaverns.paleolithicera.world.gen.treedecorator.WillowLeavesTreeDecorator
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.intprovider.ConstantIntProvider
import net.minecraft.world.gen.feature.*
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize
import net.minecraft.world.gen.foliage.BlobFoliagePlacer
import net.minecraft.world.gen.stateprovider.BlockStateProvider
import net.minecraft.world.gen.treedecorator.CocoaTreeDecorator
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator
import net.minecraft.world.gen.treedecorator.TreeDecorator
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator
import net.minecraft.world.gen.trunk.StraightTrunkPlacer

object ModConfiguredFeatures {

    val ELDERBERRY_BUSH_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("elderberry_bush")
    val CHAMOMILE_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("chamomile_plant")
    val YARROW_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("yarrow_plant")
    val WILD_GARLIC_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("wild_garlic_plant")
    val EPHEDRA_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("ephedra_plant")
    val SAGEBRUSH_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("sagebrush_plant")
    val WILLOW_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("willow")
    val WILD_MINT_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("wild_mint_plant")
    val WILD_GINGER_PLANT_CONFIGURED_KEY: RegistryKey<ConfiguredFeature<*, *>> = registerKey("wild_ginger_plant")

    fun bootstrap(context: Registerable<ConfiguredFeature<*, *>>) {
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            ELDERBERRY_BUSH_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.ELDERBERRY_BUSH)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            CHAMOMILE_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.CHAMOMILE_PLANT)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            YARROW_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.YARROW_PLANT)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            WILD_GARLIC_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.WILD_GARLIC_PLANT)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            EPHEDRA_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.EPHEDRA_PLANT)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            WILD_MINT_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.WILD_MINT_PLANT)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            SAGEBRUSH_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.SAGEBRUSH_PLANT)
        )
        Feature.RANDOM_PATCH.register<RandomPatchFeatureConfig, Feature<RandomPatchFeatureConfig>>(
            context,
            WILD_GINGER_PLANT_CONFIGURED_KEY,
            getHerbPatchConfig(ModBlocks.WILD_GINGER_PLANT)
        )

        Feature.TREE.register<TreeFeatureConfig, Feature<TreeFeatureConfig>>(
            context = context,
            key = WILLOW_CONFIGURED_KEY,
            configuration = TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.WILLOW_LOG),
                StraightTrunkPlacer(3, 5, 2),
                BlockStateProvider.of(ModBlocks.WILLOW_LEAVES),
                BlobFoliagePlacer(ConstantIntProvider.create(2),
                    ConstantIntProvider.create(0),
                    2),
                TwoLayersFeatureSize(1, 0, 0))
                    .decorators(listOf(TrunkVineTreeDecorator.INSTANCE, WillowLeavesTreeDecorator(0.55F)))
                    .ignoreVines()
            .build()
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

    private fun getHerbPatchConfig(block: Block): RandomPatchFeatureConfig {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(
            Feature.SIMPLE_BLOCK,
            SimpleBlockFeatureConfig(
                BlockStateProvider.of(
                    block.defaultState.with(
                        EdiblePlantBlock.AGE,
                        EdiblePlantBlock.MAX_AGE
                    )
                )
            ),
            listOf(Blocks.GRASS_BLOCK)
        )
    }
}