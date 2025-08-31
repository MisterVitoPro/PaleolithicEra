package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models
import net.minecraft.client.data.TexturedModel
import net.minecraft.client.data.TextureMap
import net.minecraft.client.data.TextureKey
import net.minecraft.client.data.BlockStateModelGenerator.CrossType

class ModModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {

    /**
     * Generates block state models for mod blocks.
     *
     * This function is called during data generation to create the necessary block state
     * model files for blocks added by this mod.
     *
     * @param blockStateModelGenerator The generator to register block models with
     */
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        val gen = blockStateModelGenerator
        gen.registerSimpleCubeAll(ModBlocks.BUNDLE_OF_STICKS)
        gen.registerSimpleCubeAll(ModBlocks.WILLOW_PLANKS)
        gen.registerSingleton(ModBlocks.WILLOW_LEAVES, TexturedModel.LEAVES)
        gen.registerTintableCrossBlockState(ModBlocks.WILLOW_SAPLING, CrossType.NOT_TINTED)
        gen.registerLog(ModBlocks.WILLOW_LOG).log(ModBlocks.WILLOW_LOG)
        gen.registerLog(ModBlocks.STRIPPED_WILLOW_LOG).log(ModBlocks.STRIPPED_WILLOW_LOG)
        gen.registerCubeWithCustomTextures(
            ModBlocks.KNAPPING_STATION,
            ModBlocks.KNAPPING_STATION
        ) { _, _ ->
            TextureMap()
                .put(TextureKey.UP, id("block/knapping_station_top"))
                .put(TextureKey.SIDE, id("block/knapping_station_side"))
                .put(TextureKey.DOWN, id("block/knapping_station_bottom"))
                .put(TextureKey.PARTICLE, id("block/knapping_station_side"))
        }
        gen.registerCubeWithCustomTextures(
            ModBlocks.HIDE_DRYER,
            ModBlocks.HIDE_DRYER
        ) { _, _ ->
            TextureMap()
                .put(TextureKey.UP, id("block/hide_dryer_top"))
                .put(TextureKey.SIDE, id("block/hide_dryer_side"))
                .put(TextureKey.DOWN, id("block/hide_dryer_bottom"))
                .put(TextureKey.PARTICLE, id("block/hide_dryer_side"))
        }

        // Herb plants as cross models (not tinted)
        gen.registerTintableCrossBlockState(ModBlocks.ELDERBERRY_BUSH, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.YARROW_PLANT, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.CHAMOMILE_PLANT, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.WILD_GARLIC_PLANT, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.EPHEDRA_PLANT, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.SAGEBRUSH_PLANT, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.WILD_MINT_PLANT, CrossType.NOT_TINTED)
        gen.registerTintableCrossBlockState(ModBlocks.WILD_GINGER_PLANT, CrossType.NOT_TINTED)
    }

    /**
     * Generates item models for mod items.
     *
     * This function registers all custom items with appropriate model types:
     * - GENERATED: For items displayed flat in hand (resources, materials, etc.)
     * - HANDHELD: For items displayed as tools/weapons held in hand
     *
     * @param itemModelGenerator The generator to register item models with
     */
    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        val gen = itemModelGenerator
        gen.register(ModItems.BARK, Models.GENERATED)
        gen.register(ModItems.PLANT_FIBER, Models.GENERATED)
        gen.register(ModItems.PLANT_CORDAGE, Models.GENERATED)
        gen.register(ModItems.ROCK_CHUNK, Models.GENERATED)
        gen.register(ModItems.RAWHIDE, Models.GENERATED)
        gen.register(ModItems.DRY_HIDE, Models.GENERATED)
        gen.register(ModItems.PATCHED_HIDE, Models.GENERATED)
        gen.register(ModItems.BONE_SHARD, Models.GENERATED)
        gen.register(ModItems.FLINT_BIFACE, Models.GENERATED)
        gen.register(ModItems.BOAR_SPAWN_EGG, Models.GENERATED)
        gen.register(ModItems.IBEX_SPAWN_EGG, Models.GENERATED)
        gen.register(ModItems.COOKED_ELDERBERRIES, Models.GENERATED)
        gen.register(ModItems.HIDE_LEGGINGS, Models.GENERATED)
        gen.register(ModItems.HIDE_TUNIC, Models.GENERATED)
        gen.register(ModItems.HIDE_SHOES, Models.GENERATED)
        gen.register(ModItems.HIDE_CAP, Models.GENERATED)
        gen.register(ModItems.BONE_KNIFE, Models.HANDHELD)
        gen.register(ModItems.FLINT_KNIFE, Models.HANDHELD)
        gen.register(ModItems.FIRE_DRILL, Models.HANDHELD)
        gen.register(ModItems.WOODEN_HARPOON, Models.HANDHELD)
        gen.register(ModItems.BONE_HARPOON, Models.HANDHELD)
        gen.register(ModItems.FLINT_AXE, Models.HANDHELD)
        EdiblePlants.entries.forEach { ep ->
            gen.register(ModItems.getPlantItem(ep), Models.GENERATED)
        }
    }

    // Helpers removed for client compile; datagen tasks can restore these

}
