package com.toolsandtaverns.paleolithicera.event

import com.toolsandtaverns.paleolithicera.registry.ModTags.Blocks.REQUIRES_SHOVEL
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.condition.SurvivesExplosionLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper


object BlockDropHandler {
    fun register() {
        PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvents.After { world, player, pos, state, _ ->
            val block = state.block
            val entry = Registries.BLOCK.getEntry(block)

            if (entry != null && entry.isIn(REQUIRES_SHOVEL)) {
                val heldItem = player.mainHandStack.item
                val isShovel = heldItem in listOf(
                    Items.WOODEN_SHOVEL,
                    Items.STONE_SHOVEL,
                    Items.IRON_SHOVEL,
                    Items.GOLDEN_SHOVEL,
                    Items.DIAMOND_SHOVEL,
                    Items.NETHERITE_SHOVEL
                )

                if (!isShovel && !world.isClient) {
                    // Remove all drops after the block breaks
                    // We remove them manually by setting the drops to air
                    world.setBlockState(pos, Blocks.AIR.defaultState, 3)
                    // Don't drop any items
                    // Prevent normal drop behavior by removing the loot manually
                }
            }
        })

        LootTableEvents.MODIFY.register { key, tableBuilder, _, _ ->
            val id = key.value
            // Target any block loot table whose path ends with "_leaves" (e.g., blocks/oak_leaves).
            if (id.path.startsWith("blocks/") && id.path.endsWith("_leaves")) {
                // Build a pool that drops 1 stick with a 30% chance and respects explosion decay.
                val pool = LootPool.builder()
                    .with(ItemEntry.builder(Items.STICK)) // add stick as an entry
                    .conditionally(RandomChanceLootCondition.builder(0.20f)) // 30% drop chance
                    .conditionally(SurvivesExplosionLootCondition.builder()) // standard block explosion behavior

                // Append our pool to the targeted leaves loot table.
                tableBuilder.pool(pool)
            }
        }

    }
}
