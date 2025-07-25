package com.toolsandtaverns.paleolithicera.block

import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityCollisionHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.IntProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random

/**
 * A berry bush that grows elderberries, poisonous unless cooked.
 * Behaves similarly to SweetBerryBushBlock but applies poison when eaten raw.
 */
class ElderberryBushBlock(settings: Settings) : SweetBerryBushBlock(settings) {

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        val age = state.get(AGE)
        val isMature = age == MAX_AGE
        if (isMature) {
            val dropStack = ItemStack(ModItems.RAW_ELDERBERRIES, Random.nextInt(1, 3))
            dropStack.onCraftByPlayer(player, 1)
            player.giveItemStack(dropStack)
            world.setBlockState(pos, state.with(AGE, 1))
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    override fun onEntityCollision(
        state: BlockState,
        world: World,
        pos: BlockPos,
        entity: Entity,
        handler: EntityCollisionHandler
    ) {
        // We do not want to damage or slow player
        return
    }

    companion object {
        val AGE: IntProperty = SweetBerryBushBlock.AGE
    }
}