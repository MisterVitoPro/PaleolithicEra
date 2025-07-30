package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.Constants
import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.registry.ModItems.ROCK_CHUNK
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object RockChunkLootModifier {

    fun initialize() {
        LOGGER.info("RockChunkLootModifier initializing...")
        LootTableEvents.MODIFY.register { id: RegistryKey<LootTable>, builder, source, _ ->
            // Check if we are modifying sand or gravel
            if (source.isBuiltin &&
                (id.value == Blocks.SAND.lootTableKey.get().value || id.value == Blocks.GRAVEL.lootTableKey.get().value)) {
                LOGGER.info("Adding Rock Chunk drop to: $id")
                // Inject a new loot pool with a random drop chance
                val rockChunkPool = LootPool.builder()
                    .with(ItemEntry.builder(ROCK_CHUNK)
                        .conditionally(RandomChanceLootCondition.builder(0.20f))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1f)))
                    )
                    .rolls(ConstantLootNumberProvider.create(1f))
                builder.pool(rockChunkPool)
            }
        }
    }
}