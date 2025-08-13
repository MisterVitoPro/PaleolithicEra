package com.toolsandtaverns.paleolithicera.block

import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import net.minecraft.block.BlockState
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityCollisionHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldView
import kotlin.random.Random

class YarrowPlantBlock(settings: Settings) : SweetBerryBushBlock(settings) {

    private fun getBerryItem(): Item? {
        return ModItems.EDIBLE_PLANTS[EdiblePlants.YARROW]
    }


    public override fun getPickStack(world: WorldView, pos: BlockPos, state: BlockState, b: Boolean): ItemStack {
        return ItemStack(getBerryItem())
    }

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
            val dropStack = ItemStack(getBerryItem(), Random.nextInt(1, 3))
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
        val AGE: IntProperty = Properties.AGE_3
        const val MAX_AGE: Int = 3
    }
}