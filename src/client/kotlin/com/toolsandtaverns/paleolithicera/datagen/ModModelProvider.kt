package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.block.EdiblePlantBlock
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.block.Block
import net.minecraft.client.data.*
import net.minecraft.registry.Registries
import net.minecraft.state.property.IntProperty
import net.minecraft.util.Identifier

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
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.ELDERBERRY_BUSH)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.YARROW_PLANT)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.CHAMOMILE_PLANT)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.WILD_GARLIC_PLANT)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.EPHEDRA_PLANT)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.SAGEBRUSH_PLANT)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.WILD_MINT_PLANT)
        blockStateModelGenerator.registerPlantStagesWithItem(ModBlocks.WILD_GINGER_PLANT)

        blockStateModelGenerator.registerCubeWithCustomTextures(
            ModBlocks.KNAPPING_STATION,
            ModBlocks.KNAPPING_STATION // Same block used as texture source
        ) { block, _ ->
            TextureMap()
                .put(TextureKey.UP, id("block/knapping_station_top"))
                .put(TextureKey.SIDE, Identifier.ofVanilla("block/grass_block_side"))
                .put(TextureKey.DOWN, Identifier.ofVanilla("block/dirt"))
                .put(TextureKey.PARTICLE, Identifier.ofVanilla("block/dirt"))
        }

        blockStateModelGenerator.registerCubeWithCustomTextures(
            ModBlocks.HIDE_DRYER,
            ModBlocks.HIDE_DRYER // Same block used as texture source
        ) { block, _ ->
            TextureMap()
                .put(TextureKey.UP, id("block/hide_dryer_top"))
                .put(TextureKey.SIDE, id("block/hide_dryer_side"))
                .put(TextureKey.DOWN, id("block/hide_dryer_side"))
                .put(TextureKey.PARTICLE, id("block/hide_dryer_side"))
        }
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
        // Register resource/material items with GENERATED model type
        itemModelGenerator.register(ModItems.BARK, Models.GENERATED)
        itemModelGenerator.register(ModItems.PLANT_FIBER, Models.GENERATED)
        itemModelGenerator.register(ModItems.PLANT_CORDAGE, Models.GENERATED)
        itemModelGenerator.register(ModItems.ROCK_CHUNK, Models.GENERATED)
        itemModelGenerator.register(ModItems.RAWHIDE, Models.GENERATED)
        itemModelGenerator.register(ModItems.DRY_HIDE, Models.GENERATED)
        itemModelGenerator.register(ModItems.PATCHED_HIDE, Models.GENERATED)
        itemModelGenerator.register(ModItems.BONE_SHARD, Models.GENERATED)
        itemModelGenerator.register(ModItems.FLINT_BIFACE, Models.GENERATED)
        itemModelGenerator.register(ModItems.BOAR_SPAWN_EGG, Models.GENERATED)

        // Register food items with GENERATED model type
        itemModelGenerator.register(ModItems.COOKED_ELDERBERRIES, Models.GENERATED)

        // Register armor items with GENERATED model type
        itemModelGenerator.register(ModItems.HIDE_LEGGINGS, Models.GENERATED)
        itemModelGenerator.register(ModItems.HIDE_TUNIC, Models.GENERATED)
        itemModelGenerator.register(ModItems.HIDE_SHOES, Models.GENERATED)
        itemModelGenerator.register(ModItems.HIDE_CAP, Models.GENERATED)

        // Register tools and weapons with HANDHELD model type
        itemModelGenerator.register(ModItems.BONE_KNIFE, Models.HANDHELD)
        itemModelGenerator.register(ModItems.FLINT_KNIFE, Models.HANDHELD)

        itemModelGenerator.register(ModItems.FIRE_DRILL, Models.HANDHELD)
        itemModelGenerator.register(ModItems.WOODEN_HARPOON, Models.HANDHELD)
        itemModelGenerator.register(ModItems.FLINT_AXE, Models.HANDHELD)

        EdiblePlants.entries.forEach { ediblePlants ->
            val def = ediblePlants.definitions
            val id = id(def.idPath)
            val item = Registries.ITEM.get(id)
            itemModelGenerator.register(item, Models.GENERATED)
        }
    }

    private fun BlockStateModelGenerator.registerPlantStagesWithItem(
        block: Block,
        age: IntProperty = EdiblePlantBlock.AGE
    ) {
        this.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(block).with(
                BlockStateVariantMap.models(age).generate { stage: Int? ->
                    BlockStateModelGenerator.createWeightedVariant(
                        this.createSubModel(
                            block,
                            "_stage$stage",
                            Models.CROSS,
                            { id: Identifier? -> TextureMap.cross(id) })
                    )
                })
        )
    }

}