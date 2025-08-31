package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import java.util.function.Function

class WillowLeafVinesBlock(settings: Settings) : Block(settings) {


    private val shapeFunction: Function<BlockState, VoxelShape>

    public override fun getCodec(): MapCodec<WillowLeafVinesBlock> {
        return CODEC
    }

    init {
        this.defaultState = (((((this.stateManager.getDefaultState() as BlockState).with<Boolean, Boolean>(
            UP, false
        ) as BlockState).with<Boolean, Boolean>(NORTH, false) as BlockState).with<Boolean, Boolean>(
            EAST,
            false
        ) as BlockState).with<Boolean, Boolean>(SOUTH, false) as BlockState).with<Boolean, Boolean>(WEST, false)
        this.shapeFunction = this.createShapeFunction()
    }

    private fun createShapeFunction(): Function<BlockState, VoxelShape> {
        // Build a thin face shape for each horizontal direction
        val faceShapes = mutableMapOf<Direction, VoxelShape>()
        val thickness = 1.0 / 16.0
        faceShapes[Direction.NORTH] = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, thickness)
        faceShapes[Direction.SOUTH] = VoxelShapes.cuboid(0.0, 0.0, 1.0 - thickness, 1.0, 1.0, 1.0)
        faceShapes[Direction.WEST] = VoxelShapes.cuboid(0.0, 0.0, 0.0, thickness, 1.0, 1.0)
        faceShapes[Direction.EAST] = VoxelShapes.cuboid(1.0 - thickness, 0.0, 0.0, 1.0, 1.0, 1.0)
        faceShapes[Direction.UP] = VoxelShapes.cuboid(0.0, 1.0 - thickness, 0.0, 1.0, 1.0, 1.0)

        return Function { state: BlockState ->
            var shape = VoxelShapes.empty()
            for ((dir, prop) in FACING_PROPERTIES) {
                if (state.get(prop)) {
                    val part = faceShapes[dir]
                    if (part != null) shape = VoxelShapes.union(shape, part)
                }
            }
            if (shape.isEmpty) VoxelShapes.fullCube() else shape
        }
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return this.shapeFunction.apply(state)
    }

    override fun isTransparent(state: BlockState?): Boolean {
        return true
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return this.hasAdjacentBlocks(this.getPlacementShape(state, world, pos))
    }

    private fun hasAdjacentBlocks(state: BlockState): Boolean {
        return this.getAdjacentBlockCount(state) > 0
    }

    private fun getAdjacentBlockCount(state: BlockState): Int {
        var i = 0
        val var3: MutableIterator<*> = FACING_PROPERTIES.values.iterator()

        while (var3.hasNext()) {
            val booleanProperty = var3.next() as BooleanProperty?
            if (state.get(booleanProperty)) {
                ++i
            }
        }

        return i
    }

    private fun shouldHaveSide(world: BlockView, pos: BlockPos, side: Direction): Boolean {
        if (side == Direction.DOWN) {
            return false
        } else {
            val blockPos = pos.offset(side)
            if (shouldConnectTo(world, blockPos, side)) {
                return true
            } else if (side.axis === Direction.Axis.Y) {
                return false
            } else {
                val booleanProperty = FACING_PROPERTIES[side]
                val blockState = world.getBlockState(pos.up())
                return blockState.isOf(this) && blockState.get(booleanProperty)
            }
        }
    }

    fun shouldConnectTo(world: BlockView, pos: BlockPos?, direction: Direction): Boolean {
        return MultifaceBlock.canGrowOn(world, direction, pos, world.getBlockState(pos))
    }

    private fun getPlacementShape(state: BlockState, world: BlockView, pos: BlockPos): BlockState {
        var state = state
        val blockPos = pos.up()
        if (state.get(UP)) {
            state = state.with(
                UP,
                shouldConnectTo(world, blockPos, Direction.DOWN)
            )
        }

        var blockState: BlockState? = null
        val var6: MutableIterator<*> = Direction.Type.HORIZONTAL.iterator()

        while (true) {
            var direction: Direction
            var booleanProperty: BooleanProperty?
            do {
                if (!var6.hasNext()) {
                    return state
                }

                direction = var6.next() as Direction
                booleanProperty = getFacingProperty(direction)
            } while (!state.get(booleanProperty))

            var bl = this.shouldHaveSide(world, pos, direction)
            if (!bl) {
                if (blockState == null) {
                    blockState = world.getBlockState(blockPos)
                }

                bl = blockState.isOf(this) && blockState.get(booleanProperty)
            }

            state = state.with(booleanProperty, bl)
        }
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView?,
        pos: BlockPos,
        direction: Direction?,
        neighborPos: BlockPos?,
        neighborState: BlockState?,
        random: Random?
    ): BlockState? {
        if (direction == Direction.DOWN) {
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
        } else {
            val blockState = this.getPlacementShape(state, world, pos)
            return if (!this.hasAdjacentBlocks(blockState)) Blocks.AIR.defaultState else blockState
        }
    }

    override fun canReplace(state: BlockState?, context: ItemPlacementContext): Boolean {
        val blockState = context.world.getBlockState(context.blockPos)
        return if (blockState.isOf(this)) {
            this.getAdjacentBlockCount(blockState) < FACING_PROPERTIES.size
        } else {
            super.canReplace(state, context)
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val blockState = ctx.world.getBlockState(ctx.blockPos)
        val bl = blockState.isOf(this)
        val blockState2 = if (bl) blockState else this.defaultState
        val var5 = ctx.getPlacementDirections()
        val var6 = var5.size

        for (var7 in 0..<var6) {
            val direction = var5[var7]
            if (direction != Direction.DOWN) {
                val booleanProperty = getFacingProperty(direction)
                val bl2 = bl && blockState.get(booleanProperty)
                if (!bl2 && this.shouldHaveSide(ctx.world, ctx.blockPos, direction)) {
                    return blockState2.with(booleanProperty, true)
                }
            }
        }

        return if (bl) blockState2 else null
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState? {
        when (rotation) {
            BlockRotation.CLOCKWISE_180 -> return (((state.with(
                NORTH,
                state.get(SOUTH)
            ) as BlockState).with(
                EAST,
                state.get(WEST)
            ) as BlockState).with(
                SOUTH,
                state.get(NORTH)
            ) as BlockState).with(WEST, state.get(EAST))

            BlockRotation.COUNTERCLOCKWISE_90 -> return (((state.with(
                NORTH,
                state.get(EAST)
            ) as BlockState).with(
                EAST,
                state.get(SOUTH)
            ) as BlockState).with(
                SOUTH,
                state.get(WEST)
            ) as BlockState).with(WEST, state.get(NORTH))

            BlockRotation.CLOCKWISE_90 -> return (((state.with(
                NORTH,
                state.get(WEST)
            ) as BlockState).with(
                EAST,
                state.get(NORTH)
            ) as BlockState).with(
                SOUTH,
                state.get(EAST)
            ) as BlockState).with(WEST, state.get(SOUTH))

            else -> return state
        }
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        when (mirror) {
            BlockMirror.LEFT_RIGHT -> return (state.with(
                NORTH,
                state.get(SOUTH)
            ) as BlockState).with(SOUTH, state.get(NORTH))

            BlockMirror.FRONT_BACK -> return (state.with(
                EAST,
                state.get(WEST)
            ) as BlockState).with(WEST, state.get(EAST))

            else -> return super.mirror(state, mirror)
        }
    }

    fun getFacingProperty(direction: Direction): BooleanProperty? {
        return FACING_PROPERTIES[direction]
    }

    companion object {
        val CODEC: MapCodec<WillowLeafVinesBlock> =
            createCodec<WillowLeafVinesBlock> { settings: Settings -> WillowLeafVinesBlock(settings) }
        val UP: BooleanProperty = ConnectingBlock.UP
        val NORTH: BooleanProperty = ConnectingBlock.NORTH
        val EAST: BooleanProperty = ConnectingBlock.EAST
        val SOUTH: BooleanProperty = ConnectingBlock.SOUTH
        val WEST: BooleanProperty = ConnectingBlock.WEST
        val FACING_PROPERTIES: MutableMap<Direction, BooleanProperty> =
            ConnectingBlock.FACING_PROPERTIES.entries.filter { it.key != Direction.DOWN }
                .associate { it.key to it.value }.toMutableMap()
    }

}
