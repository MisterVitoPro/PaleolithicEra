package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.block.ElderberryBushBlock
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.ItemModels
import net.minecraft.client.data.Models
import net.minecraft.client.data.TextureKey
import net.minecraft.client.data.TextureMap
import net.minecraft.util.Identifier

class ModModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        blockStateModelGenerator.registerTintableCrossBlockStateWithStages(
            ModBlocks.ELDERBERRY_BUSH,
            BlockStateModelGenerator.CrossType.NOT_TINTED,
            ElderberryBushBlock.Companion.AGE, 0, 1, 2, 3
        )
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.register(ModItems.BARK, Models.GENERATED)
        itemModelGenerator.register(ModItems.BONE_KNIFE, Models.HANDHELD)
        itemModelGenerator.register(ModItems.BONE_SPEAR, Models.HANDHELD)
        itemModelGenerator.register(ModItems.BONE_SPEARHEAD, Models.GENERATED)
        itemModelGenerator.register(ModItems.FIRE_DRILL, Models.HANDHELD)
        itemModelGenerator.register(ModItems.FLINT_SHARD, Models.GENERATED)
        itemModelGenerator.register(ModItems.PLANT_FIBER, Models.GENERATED)
        itemModelGenerator.register(ModItems.RAW_ELDERBERRIES, Models.GENERATED)
        itemModelGenerator.register(ModItems.COOKED_ELDERBERRIES, Models.GENERATED)
    }

}