package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.PaleolithicEra.logger
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
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class LootTableGenerator(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricBlockLootTableProvider(output, registryLookup) {

    override fun generate() {
        // ✅ Add your custom block
        addDrop(ModBlocks.KNAPPING_STATION)

        // ✅ Get the bark item
        val bark = Registries.ITEM.get(Identifier.of(MOD_ID, "bark"))

        // ✅ List of vanilla logs and stems (feel free to expand manually)
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

        // ✅ Add bark drop to each known log
        knownLogs.forEach { addBarkToLogDrop(it, bark) }

        addPlantFiberDrop(net.minecraft.block.Blocks.TALL_GRASS)
        addPlantFiberDrop(net.minecraft.block.Blocks.LARGE_FERN)
    }


    private fun addBarkToLogDrop(log: Block, bark: Item) {
        val logDrop = ItemEntry.builder(log.asItem())
        val barkDrop = ItemEntry.builder(bark)
            .conditionally(RandomChanceLootCondition.builder(0.35f)) // 35% chance

        val pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1f))
            .with(logDrop)
            .with(barkDrop)

        val lootTable = LootTable.builder().pool(pool)
        addDrop(log, lootTable)
    }

    private fun addPlantFiberDrop(grass: Block) {
        val fiberDrop = ItemEntry.builder(Registries.ITEM.get(Identifier.of(MOD_ID, "plant_fiber")))
            .conditionally(RandomChanceLootCondition.builder(0.30f)) // 30% chance

        val pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1f))
            .with(ItemEntry.builder(grass.asItem())) // Still drops itself (or nothing)
            .with(fiberDrop)

        val table = LootTable.builder().pool(pool)

        this.addDrop(grass, table)
    }
}
