package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.block.entity.CrudeCampfireBlockEntity
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

    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return 20 // seconds (20 ticks/sec)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
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

            if ((state.block is CampfireBlock || state.block == ModBlocks.CRUDE_CAMPFIRE) && !state.get(Properties.LIT)) {
                world.setBlockState(pos, state.with(Properties.LIT, true))
                world.playSound(
                    null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f
                )

                val blockEntity: CrudeCampfireBlockEntity? = world.getBlockEntity(pos) as? CrudeCampfireBlockEntity
                blockEntity?.startBurnTimer()



                if (!world.isClient && user is ServerPlayerEntity) {
                    ModCriteria.LIT_CRUDE_CAMPFIRE.trigger(user)
                }

                // Damage item
                stack.damage(1, user, Hand.MAIN_HAND)
            }
        }

        return stack
    }
}
