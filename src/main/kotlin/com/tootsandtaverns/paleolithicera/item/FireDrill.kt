package com.tootsandtaverns.paleolithicera.item

import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.consume.UseAction
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A primitive tool that lights unlit campfires when crouching and holding use for 3 seconds.
 * Breaks after 10 uses.
 */
class FireDrillItem(settings: Settings) : Item(settings) {

    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return 60 // 3 seconds (20 ticks/sec)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        val stack = user.getStackInHand(hand)
        user.setCurrentHand(hand)
        return ActionResult.SUCCESS
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BOW // shows "charging" animation
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        if (user !is PlayerEntity || !user.isSneaking) return stack

        val hitResult = user.raycast(5.0, 0f, false)
        if (hitResult is BlockHitResult) {
            val pos: BlockPos = hitResult.blockPos
            val state: BlockState = world.getBlockState(pos)

            if (state.block is CampfireBlock && !state.get(CampfireBlock.LIT)) {
                world.setBlockState(pos, state.with(CampfireBlock.LIT, true))
                world.playSound(
                    null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f
                )

                // Damage item (break after 10 uses)
                stack.damage(1, user, Hand.MAIN_HAND)
            }
        }

        return stack
    }
}
