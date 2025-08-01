package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.entity.CrudeCampfireBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModCriteria
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.consume.UseAction
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A primitive tool that lights unlit campfires when crouching and holding use for n seconds.
 */
class FireDrillItem(settings: Settings) : Item(settings) {

    /**
     * Determines how long the item needs to be used to complete the action.
     * 
     * @param stack The item stack being used
     * @param user The entity using the item
     * @return The number of ticks (3 seconds at 20 TPS)
     */
    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return 60 // 3 seconds at 20 ticks per second
    }

    /**
     * Handles right-click usage of the fire drill.
     * 
     * Starts the use animation when right-clicked.
     * 
     * @param world The world in which the item is being used
     * @param user The player using the item
     * @param hand The hand in which the item is being held
     * @return SUCCESS to indicate the action was handled
     */
    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        user.setCurrentHand(hand) // Start the use animation
        return ActionResult.SUCCESS
    }

    /**
     * Determines the animation to play when using this item.
     * 
     * @param stack The item stack being used
     * @return BOW use action to show a charging animation
     */
    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BOW // Use bow animation to show "drilling" action
    }

    /**
     * Called when the player finishes using the fire drill (after holding right-click).
     * 
     * If the player is sneaking and looking at an unlit campfire, this will light the campfire,
     * play a sound effect, and damage the fire drill item.
     * 
     * @param stack The item stack being used
     * @param world The world in which the item is being used
     * @param user The entity using the item
     * @return The possibly modified item stack
     */
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        // Only proceed if user is a player and is sneaking (to prevent accidental use)
        if (user !is PlayerEntity || !user.isSneaking) return stack

        // Cast a ray from player's view to find what they're looking at
        val hitResult = user.raycast(5.0, 0f, false)
        if (hitResult is BlockHitResult) {
            val pos: BlockPos = hitResult.blockPos
            val state: BlockState = world.getBlockState(pos)

            // Check if the block is an unlit campfire (vanilla or crude)
            if ((state.block is CampfireBlock || state.block == ModBlocks.CRUDE_CAMPFIRE) && !state.get(Properties.LIT)) {
                // Light the campfire by updating its blockstate
                world.setBlockState(pos, state.with(Properties.LIT, true))
                // Play the flint and steel sound effect
                world.playSound(
                    null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f
                )

                // If it's our custom campfire, start its burn timer
                val blockEntity: CrudeCampfireBlockEntity? = world.getBlockEntity(pos) as? CrudeCampfireBlockEntity
                blockEntity?.startBurnTimer()

                // Grant the advancement for lighting a crude campfire (server-side only)
                if (!world.isClient && user is ServerPlayerEntity) {
                    ModCriteria.LIT_CRUDE_CAMPFIRE.trigger(user)
                }

                // Damage the fire drill item by 1 durability point
                stack.damage(1, user, Hand.MAIN_HAND)
            }
        }

        return stack
    }
}
