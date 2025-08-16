package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.block.EdiblePlantBlock.Companion.AGE
import com.toolsandtaverns.paleolithicera.block.EdiblePlantBlock.Companion.MAX_AGE
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.BlockStatePropertyLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.predicate.StatePredicate
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class LootTableProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricBlockLootTableProvider(output, registryLookup) {

    override fun generate() {
        addDrop(ModBlocks.KNAPPING_STATION)
        addDrop(ModBlocks.HIDE_DRYER)
        // Herbs
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.ELDERBERRY]!!.asItem(), ModBlocks.ELDERBERRY_BUSH)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.CHAMOMILE]!!.asItem(), ModBlocks.CHAMOMILE_PLANT)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.YARROW]!!.asItem(), ModBlocks.YARROW_PLANT)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.WILD_GARLIC]!!.asItem(), ModBlocks.WILD_GARLIC_PLANT)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.EPHEDRA]!!.asItem(), ModBlocks.EPHEDRA_PLANT)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.SAGEBRUSH]!!.asItem(), ModBlocks.SAGEBRUSH_PLANT)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.WILD_MINT]!!.asItem(), ModBlocks.WILD_MINT_PLANT)
        addPlantDrops(ModItems.EDIBLE_PLANTS[EdiblePlants.WILD_GINGER]!!.asItem(), ModBlocks.WILD_GINGER_PLANT)

        listOf(
            Blocks.OAK_LOG,
            Blocks.SPRUCE_LOG,
            Blocks.BIRCH_LOG,
            Blocks.JUNGLE_LOG,
            Blocks.ACACIA_LOG,
            Blocks.DARK_OAK_LOG,
            Blocks.MANGROVE_LOG,
            Blocks.CHERRY_LOG,
            Blocks.BAMBOO_BLOCK,
            Blocks.CRIMSON_STEM,
            Blocks.WARPED_STEM
        ).forEach { addBarkToLogDrop(it, Registries.ITEM.get(id( "bark"))) }

    }

    private fun addBarkToLogDrop(log: Block, bark: Item) {
        val logDrop: LeafEntry.Builder<*> = ItemEntry.builder(log.asItem())
        val barkDrop: LeafEntry.Builder<*> = ItemEntry.builder(bark)
            .conditionally(RandomChanceLootCondition.builder(0.40f))

        val pool: LootPool.Builder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1f))
            .with(logDrop)
            .with(barkDrop)

        val lootTable: LootTable.Builder = LootTable.builder().pool(pool)
        addDrop(log, lootTable)
    }

    private fun addPlantDrops(dropItem: Item, block: Block) {
        addDrop(
            block, LootTable.builder()
                .pool(
                    LootPool.builder()
                        .conditionally(
                            BlockStatePropertyLootCondition.builder(block)
                                .properties(StatePredicate.Builder.create().exactMatch(AGE, MAX_AGE))
                        )
                        .rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(dropItem))
                )
        )
    }

}
