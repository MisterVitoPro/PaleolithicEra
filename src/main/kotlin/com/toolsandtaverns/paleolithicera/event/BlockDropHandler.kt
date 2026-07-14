package com.toolsandtaverns.paleolithicera.event

import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.condition.SurvivesExplosionLootCondition
import net.minecraft.loot.entry.ItemEntry


object BlockDropHandler {
    fun register() {
        LootTableEvents.MODIFY.register { key, tableBuilder, _, _ ->
            val id = key.value
            // Target any block loot table whose path ends with "_leaves" (e.g., blocks/oak_leaves).
            if (id.path.startsWith("blocks/") && id.path.endsWith("_leaves")) {
                // Build a pool that drops 1 stick with a 30% chance and respects explosion decay.
                val pool = LootPool.builder()
                    .with(ItemEntry.builder(Items.STICK)) // add stick as an entry
                    .conditionally(RandomChanceLootCondition.builder(0.30f))
                    .conditionally(SurvivesExplosionLootCondition.builder()) // standard block explosion behavior

                // Append our pool to the targeted leaves loot table.
                tableBuilder.pool(pool)
            }
        }

    }
}
