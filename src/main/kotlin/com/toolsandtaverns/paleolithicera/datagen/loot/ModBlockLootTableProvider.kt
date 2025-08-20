package com.toolsandtaverns.paleolithicera.datagen.loot

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
import java.util.concurrent.CompletableFuture

class ModBlockLootTableProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricBlockLootTableProvider(output, registryLookup) {

    override fun generate() {
        addDrop(ModBlocks.KNAPPING_STATION)
        addDrop(ModBlocks.HIDE_DRYER)
        // Herbs
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.ELDERBERRY), ModBlocks.ELDERBERRY_BUSH)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.CHAMOMILE), ModBlocks.CHAMOMILE_PLANT)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.YARROW), ModBlocks.YARROW_PLANT)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.WILD_GARLIC), ModBlocks.WILD_GARLIC_PLANT)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.EPHEDRA), ModBlocks.EPHEDRA_PLANT)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.SAGEBRUSH), ModBlocks.SAGEBRUSH_PLANT)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.WILD_MINT), ModBlocks.WILD_MINT_PLANT)
        addPlantDrops(ModItems.getPlantItem(EdiblePlants.WILD_GINGER), ModBlocks.WILD_GINGER_PLANT)

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

        // Willow Tree
        addDrop(ModBlocks.WILLOW_LOG)
        addDrop(ModBlocks.STRIPPED_WILLOW_LOG)
        addDrop(ModBlocks.WILLOW_PLANKS)
        addDrop(ModBlocks.WILLOW_SAPLING)
        addDrop(
            ModBlocks.WILLOW_LEAVES,
            leavesDrops(ModBlocks.WILLOW_LEAVES, ModBlocks.WILLOW_SAPLING, 0.0625f)
        )

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
