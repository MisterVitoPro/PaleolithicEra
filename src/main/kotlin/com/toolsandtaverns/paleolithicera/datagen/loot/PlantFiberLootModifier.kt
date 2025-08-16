package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModTags
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

/**
 * Loot table modifier that adds plant fiber drops to various grass blocks.
 *
 * This class modifies vanilla loot tables to add a chance for plant fiber
 * to drop when cutting grass blocks with a knife tool.
 */
object PlantFiberLootModifier {

    /**
     * List of vanilla block loot tables that should have plant fiber drops added.
     *
     * These are the loot table IDs for various grass-type blocks that can
     * yield plant fiber when harvested with a knife.
     */
    val listOfBlocksForDrops: List<Identifier> = listOf(
        "blocks/tall_grass",
        "blocks/large_fern",
        "blocks/short_grass",
        "blocks/short_dry_grass",
        "blocks/tall_dry_grass",
    )
        .map { Identifier.ofVanilla(it) } // Convert string IDs to Identifier objects
        .toList()

    /**
     * Initializes the loot table modifications for plant fiber drops.
     *
     * This method registers an event handler that adds plant fiber as a potential drop
     * from grass blocks when harvested with a knife tool. The fiber has a 40% chance
     * to drop when the conditions are met.
     */
    fun initialize() {
        LootTableEvents.MODIFY.register { id, tableBuilder, _, registryLookup ->
            // Check if the current loot table is one we want to modify
            if (listOfBlocksForDrops.contains(id.value)) {
                // Get the item registry for tag lookups
                val itemLookup: RegistryEntryLookup<Item> = registryLookup.getOrThrow(RegistryKeys.ITEM)

                // Create a drop entry for plant fiber with conditions
                val fiberDrop = ItemEntry.builder(ModItems.PLANT_FIBER)
                    .conditionally(
                        // Only drop when harvested with a knife (any item with the KNIFE tag)
                        MatchToolLootCondition.builder(
                            ItemPredicate.Builder.create().tag(itemLookup, ModTags.Items.KNIFE)
                        )
                    )
                    .conditionally(RandomChanceLootCondition.builder(0.4f)) // 40% drop chance

                // Create a loot pool with one roll and no bonus rolls
                val fiberPool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1f)) // Always roll once
                    .bonusRolls(ConstantLootNumberProvider.create(0f)) // No bonus rolls
                    .with(fiberDrop) // Add the fiber drop to the pool

                // Add our new loot pool to the existing table
                tableBuilder.pool(fiberPool)
            }
        }
    }
}