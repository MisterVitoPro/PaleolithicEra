package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.entity.EntityType
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
        addDrop(ModBlocks.ELDERBERRY_BUSH, addElderberryBushesDrop(ModItems.RAW_ELDERBERRIES))

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
        ).forEach { addBarkToLogDrop(it, Registries.ITEM.get(Identifier.of(MOD_ID, "bark"))) }

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

    private fun addElderberryBushesDrop(dropItem: Item): LootTable.Builder {
        return LootTable.builder()
            .pool(
                LootPool.builder()
                    .conditionally(
                        BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH)
                        .properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3)))
                    .rolls(ConstantLootNumberProvider.create(1f))
                    .with(ItemEntry.builder(dropItem))
            )
    }

}
