package com.toolsandtaverns.paleolithicera.event

import com.toolsandtaverns.paleolithicera.registry.ModTags.Blocks.REQUIRES_SHOVEL
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.registry.Registries


object BlockDropHandler {
    fun register() {
        // WORKS - KINDA
//        PlayerBlockBreakEvents.BEFORE.register(PlayerBlockBreakEvents.Before { world, player, pos, state, blockEntity ->
//            val block = state.block
//            val entry = Registries.BLOCK.getEntry(block)
//
//            if (entry != null && entry.isIn(REQUIRES_SHOVEL)) {
//                val heldItem = player.mainHandStack.item
//                val isShovel = heldItem in listOf(
//                    Items.WOODEN_SHOVEL,
//                    Items.STONE_SHOVEL,
//                    Items.IRON_SHOVEL,
//                    Items.GOLDEN_SHOVEL,
//                    Items.DIAMOND_SHOVEL,
//                    Items.NETHERITE_SHOVEL
//                )
//
//                if (!isShovel) {
//                    if (!world.isClient) {
//                        player.sendMessage(Text.literal("You need a shovel to dig this properly."), true)
//                    }
//                    return@Before false // cancels the break entirely
//                }
//            }
//
//            return@Before true // allow normal breaking
//        })

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
    }
}
