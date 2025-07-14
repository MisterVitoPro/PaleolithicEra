package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class LootTableGenerator(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricBlockLootTableProvider(output, registryLookup) {

    override fun generate() {
        addDrop(ModBlocks.KNAPPING_STATION)

        val bark: Item = Registries.ITEM.get(Identifier.of(MOD_ID, "bark"))

        val knownLogs: List<Block> = listOf(
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
        )

        knownLogs.forEach { addBarkToLogDrop(it, bark) }
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

}
