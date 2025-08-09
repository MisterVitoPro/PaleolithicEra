package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.CrudeBedBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.enums.BedPart
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootWorldContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class CrudeBedBlock(settings: Settings) : HorizontalFacingBlock(settings), BlockEntityProvider {

    companion object {
        val PART: EnumProperty<BedPart> = Properties.BED_PART
        val OCCUPIED: BooleanProperty = Properties.OCCUPIED
        val CODEC: MapCodec<CrudeBedBlock> = createCodec(::CrudeBedBlock)
    }

    init {
        this.defaultState = ((this.stateManager.getDefaultState() as BlockState).with<BedPart, BedPart>(
            PART,
            BedPart.FOOT
        ) as BlockState).with<Boolean, Boolean>(OCCUPIED, false)
    }

    override fun onLandedUpon(
        world: World,
        state: BlockState,
        pos: BlockPos,
        entity: Entity,
        fallDistance: Double
    ) {
        super.onLandedUpon(world, state, pos, entity, fallDistance * 0.5)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState {
        if (direction == getDirectionTowardsOtherPart(
                state.get(PART),
                state.get(FACING)
            )
        ) {
            return (if (neighborState.isOf(this) && neighborState.get(PART) != state.get(PART)) state.with(
                OCCUPIED,
                neighborState.get(OCCUPIED) as Boolean
            ) as BlockState else Blocks.AIR.defaultState)
        } else {
            return super.getStateForNeighborUpdate(
                state,
                world,
                tickView,
                pos,
                direction,
                neighborPos,
                neighborState,
                random
            )
        }
    }

    private fun getDirectionTowardsOtherPart(part: BedPart?, direction: Direction): Direction? {
        return if (part == BedPart.FOOT) direction else direction.opposite
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val direction = ctx.horizontalPlayerFacing
        val blockPos = ctx.blockPos
        val blockPos2 = blockPos.offset(direction)
        val world = ctx.world
        return if (world.getBlockState(blockPos2).canReplace(ctx) && world.worldBorder
                .contains(blockPos2)
        ) this.defaultState.with(FACING, direction) else null
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState? {
        if (!world.isClient && player.shouldSkipBlockDrops()) {
            val bedPart = state.get(PART)
            if (bedPart == BedPart.FOOT) {
                val blockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.get(FACING)))
                val blockState = world.getBlockState(blockPos)
                if (blockState.isOf(this) && blockState.get(PART) == BedPart.HEAD) {
                    world.setBlockState(blockPos, Blocks.AIR.defaultState, NOTIFY_ALL or SKIP_DROPS)
                    world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, getRawIdFromState(blockState))
                }
            }
        }

        return super.onBreak(world, pos, state, player)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        if (state.get(PART) == BedPart.HEAD) {
            player.sendMessage(Text.literal("Use the foot of the bed to rest"), true)
            return ActionResult.SUCCESS
        }

        if (player.hungerManager.foodLevel >= 4 && player.health < player.maxHealth) {
            player.heal(4.0f)
            player.hungerManager.add(-4, 0f)  // Remove 4 food points
            player.sendMessage(Text.literal("You rest for a moment..."), true)
            return ActionResult.CONSUME
        }

        player.sendMessage(Text.literal("You do not need rest at the moment."), true)
        return ActionResult.SUCCESS
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (!world.isClient) {
            val blockPos = pos.offset(state.get(FACING))
            world.setBlockState(blockPos, state.with(BedBlock.PART, BedPart.HEAD), NOTIFY_ALL)
            world.updateNeighbors(pos, Blocks.AIR)
            state.updateNeighbors(world, pos, NOTIFY_ALL)
        }
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean {
        return false
    }

    override fun getDroppedStacks(state: BlockState, builder: LootWorldContext.Builder): List<ItemStack> {
        // Only drop the item once (from the foot)
        return if (state.get(PART) == BedPart.FOOT) {
            listOf(ItemStack(this))
        } else {
            emptyList()
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, PART, OCCUPIED)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CrudeBedBlockEntity(pos, state)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? {
        return CODEC
    }


}
