package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.EffigyOfProtectionEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

/**
 * Final effigy block that holds bones as fuel and emits a protective aura
 * preventing mobs from targeting players within a radius while active.
 */
class EffigyOfProtectionBlock(settings: Settings) : BlockWithEntity(settings) {

    companion object {
        val FACING = Properties.HORIZONTAL_FACING
        
        val CODEC: MapCodec<EffigyOfProtectionBlock> by lazy {
            createCodec { EffigyOfProtectionBlock(Settings.create().strength(1.5f).nonOpaque()) }
        }

        // Lower shape (y 0..16): base + rim + lower center pole
        private val LOWER_SHAPE: VoxelShape by lazy {
            VoxelShapes.union(
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),   // base
                createCuboidShape(3.0, 8.0, 3.0, 14.0, 11.0, 14.0),  // mid rim
                createCuboidShape(6.0, 11.0, 6.0, 10.0, 16.0, 10.0)  // center pole lower slice
            )
        }

        // Minimum distance between effigies (blocks)
        private const val MIN_DIST: Int = 16

        private fun isNearAnotherEffigy(world: WorldView, pos: BlockPos): Boolean {
            val min = pos.add(-MIN_DIST, -MIN_DIST, -MIN_DIST)
            val max = pos.add(MIN_DIST, MIN_DIST, MIN_DIST)
            BlockPos.iterate(min, max).forEach { p ->
                if (p != pos && world.getBlockState(p).isOf(ModBlocks.EFFIGY_OF_PROTECTION)) {
                    return true
                }
            }
            return false
        }
    }

    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> = CODEC

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: net.minecraft.block.ShapeContext
    ): VoxelShape {
        return LOWER_SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: net.minecraft.block.ShapeContext
    ): VoxelShape {
        return LOWER_SHAPE
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return EffigyOfProtectionEntity(pos, state)
    }

    //? if <=1.21.4 {
    /*override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (!state.isOf(newState.block)) {
            (world.getBlockEntity(pos) as? EffigyOfProtectionEntity)?.dropItems(world, pos)
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }*///?}

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val pos = ctx.blockPos
        val world = ctx.world
        // Prevent placement if another effigy is too close
        if (isNearAnotherEffigy(world, pos)) return null
        // Ensure there is space for the top part
        if (!world.getBlockState(pos.up()).canReplace(ctx)) return null
        // Face the player when placed
        return defaultState.with(FACING, ctx.horizontalPlayerFacing.opposite)
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return super.canPlaceAt(state, world, pos) && !isNearAnotherEffigy(world, pos)
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient && type === ModEntityType.EFFIGY_OF_PROTECTION_BLOCK_ENTITY) {
            BlockEntityTicker { w, p, s, be -> (be as? EffigyOfProtectionEntity)?.serverTick(w as ServerWorld) }
        } else null
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        val be = world.getBlockEntity(pos) as? EffigyOfProtectionEntity ?: return ActionResult.PASS
        var held: ItemStack = player.mainHandStack
        if (held.isEmpty) held = player.offHandStack

        // Insert bones if held
        if (!held.isEmpty && held.isOf(Items.BONE)) {
            val inserted = be.tryInsertFuel(held)
            if (inserted > 0) {
                held.decrement(inserted)
                return ActionResult.CONSUME
            }
            return ActionResult.PASS
        }

        // Sneak to extract stack
        if (held.isEmpty && player.isSneaking) {
            val extracted = be.extractAll()
            if (!extracted.isEmpty) {
                if (!player.giveItemStack(extracted)) {
                    player.dropItem(extracted, false)
                }
                return ActionResult.CONSUME
            }
        }

        return ActionResult.SUCCESS
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING)))
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(FACING)))
    }

    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        if (!world.isClient) {
            val above = pos.up()
            val st = world.getBlockState(above)
            if (!st.isOf(ModBlocks.EFFIGY_OF_PROTECTION_TOP)) {
                // Heal missing top half after placements or conversions
                if (st.isAir) {
                    world.setBlockState(above, ModBlocks.EFFIGY_OF_PROTECTION_TOP.defaultState, Block.NOTIFY_ALL)
                }
            }
        }
    }


    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: net.minecraft.entity.LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (!world.isClient) {
            val above = pos.up()
            if (world.getBlockState(above).isAir) {
                world.setBlockState(above, ModBlocks.EFFIGY_OF_PROTECTION_TOP.defaultState, Block.NOTIFY_ALL)
            }
        }
    }


    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState? {
        if (!world.isClient) {
            val above = pos.up()
            val topState = world.getBlockState(above)
            if (topState.isOf(ModBlocks.EFFIGY_OF_PROTECTION_TOP)) {
                world.breakBlock(above, false, player)
            }
        }
        return super.onBreak(world, pos, state, player)
    }
}
