package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.FoodDryerBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

/**
 * Top half of the Food Dryer structure.
 * 
 * This block provides the upper visual component of the 2-block tall food dryer
 * while delegating all interactions to the main FoodDryerBlock below.
 * The top block contains the hanging framework where food items are visually
 * displayed during the drying process.
 */
class FoodDryerTopBlock(settings: Settings) : Block(settings) {

    override fun getCodec(): MapCodec<out Block> = CODEC

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: net.minecraft.block.ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: net.minecraft.block.ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        
        // Delegate interaction to the main food dryer block below
        val belowPos = pos.down()
        val belowState = world.getBlockState(belowPos)
        
        if (belowState.isOf(ModBlocks.FOOD_DRYER)) {
            val blockEntity = world.getBlockEntity(belowPos)
            if (blockEntity is FoodDryerBlockEntity) {
                player.openHandledScreen(belowState.createScreenHandlerFactory(world, belowPos))
                return ActionResult.CONSUME
            }
        }
        
        return ActionResult.PASS
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState? {
        if (!world.isClient) {
            val below = pos.down()
            val belowState = world.getBlockState(below)
            if (belowState.isOf(ModBlocks.FOOD_DRYER)) {
                world.breakBlock(below, !player.shouldCancelInteraction(), player)
            }
        }
        return super.onBreak(world, pos, state, player)
    }

    companion object {
        val CODEC: MapCodec<FoodDryerTopBlock> by lazy {
            createCodec { FoodDryerTopBlock(Settings.create().strength(1.5f).nonOpaque()) }
        }

        // Top portion shape - continues the frame structure and provides hanging points
        private val SHAPE: VoxelShape by lazy {
            VoxelShapes.union(
                // Four corner posts (continuation from below)
                createCuboidShape(1.0, 0.0, 1.0, 3.0, 8.0, 3.0),
                createCuboidShape(13.0, 0.0, 1.0, 15.0, 8.0, 3.0),
                createCuboidShape(1.0, 0.0, 13.0, 3.0, 8.0, 15.0),
                createCuboidShape(13.0, 0.0, 13.0, 15.0, 8.0, 15.0),
                // Top frame connecting posts
                createCuboidShape(1.0, 6.0, 1.0, 15.0, 8.0, 3.0),
                createCuboidShape(1.0, 6.0, 13.0, 15.0, 8.0, 15.0),
                createCuboidShape(1.0, 6.0, 3.0, 3.0, 8.0, 13.0),
                createCuboidShape(13.0, 6.0, 3.0, 15.0, 8.0, 13.0),
                // Additional cross beams for item hanging
                createCuboidShape(3.0, 6.0, 7.0, 13.0, 8.0, 9.0)
            )
        }
    }
}