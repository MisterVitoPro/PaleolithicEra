package com.toolsandtaverns.paleolithicera.event

import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModTags.Blocks.REQUIRES_SHOVEL
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.minecraft.block.Blocks
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction


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

//        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
//            val stack = player.getStackInHand(hand)
//            val pos: BlockPos = hitResult.blockPos
//            val state = world.getBlockState(pos)
//
//            // Only trigger if holding an axe and block is strippable
//            if (stack.item is AxeItem && StrippableBlockRegistry.get(state.block) != null) {
//                if (!world.isClient && world is ServerWorld) {
//                    val dropCount = world.random.nextBetween(1, 3)
//                    val bark = ItemStack(ModItems.BARK, dropCount)
//                    val dropPos = pos.offset(Direction.UP)
//                    BlockPos.dropStack(world, dropPos, bark)
//                }
//            }
//
//            ActionResult.PASS // Let vanilla stripping happen
//        })
    }
}
