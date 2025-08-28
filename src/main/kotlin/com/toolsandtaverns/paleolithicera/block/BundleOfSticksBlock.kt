package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModCriteria
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.server.world.ServerWorld

/**
 * Simple intermediate block used to construct an Effigy of Protection.
 * When placed between gravel (below) and a bone block (above), it converts
 * the structure into the effigy block and consumes the gravel and bone block.
 */
class BundleOfSticksBlock(settings: Settings) : Block(settings) {

    override fun getCodec(): MapCodec<out Block> = CODEC

    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        if (!world.isClient) tryCreateEffigy(world, pos)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: net.minecraft.world.WorldView,
        tickView: net.minecraft.world.tick.ScheduledTickView?,
        pos: BlockPos,
        direction: Direction?,
        neighborPos: BlockPos?,
        neighborState: BlockState?,
        random: net.minecraft.util.math.random.Random?
    ): BlockState? {
        if (world is World && !world.isClient && (direction == Direction.UP || direction == Direction.DOWN)) {
            tryCreateEffigy(world, pos)
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random)
    }

    private fun tryCreateEffigy(world: World, pos: BlockPos) {
        val below = world.getBlockState(pos.down())
        val above = world.getBlockState(pos.up())
        if (below.isOf(Blocks.GRAVEL) && above.isOf(Blocks.BONE_BLOCK)) {
            // Prevent conversion if another effigy is too close to this structure
            if (isNearAnotherEffigy(world, pos)) return
            // Replace this position with the Effigy (lower) and clear above/below
            world.breakBlock(pos, false)
            world.breakBlock(pos.up(), false)
            world.breakBlock(pos.down(), false)
            val basePos = pos.down()
            world.setBlockState(basePos, ModBlocks.EFFIGY_OF_PROTECTION.defaultState)
            // Place the invisible top to make the full-height model interactable
            val topPos = basePos.up()
            if (world.getBlockState(topPos).isAir) {
                world.setBlockState(topPos, ModBlocks.EFFIGY_OF_PROTECTION_TOP.defaultState)
            }
            // Trigger advancement for the nearest player (the placer)
            if (world is ServerWorld) {
                val p = world.getClosestPlayer(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 6.0, false)
                if (p is net.minecraft.server.network.ServerPlayerEntity) {
                    ModCriteria.EFFIGY_PLACED.trigger(p)
                }
            }
        }
    }

    private fun isNearAnotherEffigy(world: World, pos: BlockPos): Boolean {
        val minDist = 16
        val min = pos.add(-minDist, -minDist, -minDist)
        val max = pos.add(minDist, minDist, minDist)
        var found = false
        BlockPos.iterate(min, max).forEach { p ->
            if (p != pos && world.getBlockState(p).isOf(ModBlocks.EFFIGY_OF_PROTECTION)) {
                found = true
                return@forEach
            }
        }
        return found
    }

    companion object {
        val CODEC: MapCodec<BundleOfSticksBlock> by lazy {
            createCodec { BundleOfSticksBlock(Settings.create().strength(0.5f)) }
        }
    }
}
