package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.CrudeBedBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.enums.BedPart
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerEntity.SleepFailureReason
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
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.World.ExplosionSourceType
import net.minecraft.world.WorldEvents
import net.minecraft.world.WorldView
import net.minecraft.world.explosion.ExplosionBehavior
import net.minecraft.world.tick.ScheduledTickView
import java.util.function.Consumer
import java.util.function.Predicate

class CrudeBedBlock(settings: Settings) : HorizontalFacingBlock(settings), BlockEntityProvider {

    companion object {
        val PART: EnumProperty<BedPart> = Properties.BED_PART
        val OCCUPIED: BooleanProperty = Properties.OCCUPIED
        val CODEC: MapCodec<CrudeBedBlock> = createCodec(::CrudeBedBlock)

        fun isBedWorking(world: World): Boolean {
            return world.dimension.bedWorks()
        }
    }

    init {
        this.defaultState = ((this.stateManager.getDefaultState() as BlockState).with<BedPart, BedPart>(
            PART,
            BedPart.FOOT
        ) as BlockState).with<Boolean, Boolean>(OCCUPIED, false)
    }

    fun getDirection(world: BlockView, pos: BlockPos): Direction? {
        val blockState = world.getBlockState(pos)
        return if (blockState.block is CrudeBedBlock) blockState.get(FACING) else null
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS_SERVER
        } else {
            if (state.get(PART) != BedPart.HEAD) {
                val pos = pos.offset(state.get(FACING))
                val state = world.getBlockState(pos)
                if (!state.isOf(this)) {
                    return ActionResult.CONSUME
                }
            }

            if (!isBedWorking(world)) {
                world.removeBlock(pos, false)
                val blockPos = pos.offset((state.get(FACING) as Direction).opposite)
                if (world.getBlockState(blockPos).isOf(this)) {
                    world.removeBlock(blockPos, false)
                }

                val vec3d = pos.toCenterPos()
                world.createExplosion(
                    null,
                    world.damageSources.badRespawnPoint(vec3d),
                    null,
                    vec3d,
                    5.0f,
                    true,
                    ExplosionSourceType.BLOCK
                )
                return ActionResult.SUCCESS_SERVER
            } else if (state.get(OCCUPIED)) {
                if (!this.wakeVillager(world, pos)) {
                    player.sendMessage(Text.translatable("block.minecraft.bed.occupied"), true)
                }

                return ActionResult.SUCCESS_SERVER
            } else {
                player.trySleep(pos).ifLeft(Consumer { reason: SleepFailureReason? ->
                    if (reason?.message != null) {
                        player.sendMessage(reason.message, true)
                    }
                })
                return ActionResult.SUCCESS_SERVER
            }
        }
    }

    private fun wakeVillager(world: World, pos: BlockPos): Boolean {
        val list = world.getEntitiesByClass(
            VillagerEntity::class.java,
            Box(pos),
            Predicate { obj: VillagerEntity -> obj.isSleeping })
        if (list.isEmpty()) {
            return false
        } else {
            (list[0] as VillagerEntity).wakeUp()
            return true
        }
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
