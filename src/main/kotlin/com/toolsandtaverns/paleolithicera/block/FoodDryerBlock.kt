package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.FoodDryerBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

/**
 * Food Dryer Block - A 2-block tall multiblock structure for drying food items.
 * 
 * The Food Dryer represents primitive food preservation technology, allowing players
 * to convert raw food items into dried versions for long-term storage. This process
 * simulates traditional air-drying methods used by early humans.
 * 
 * Key features:
 * - 2-block tall multiblock structure (bottom functional, top visual)
 * - 4-slot inventory system with independent drying timers
 * - Items visually hang on the dryer structure while processing
 * - Only accepts food items that have drying recipes
 * - Each slot processes one item at a time with visual progress
 */
class FoodDryerBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun getCodec(): MapCodec<out BlockWithEntity> = CODEC

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

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return FoodDryerBlockEntity(pos, state)
    }

    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        return blockEntity as FoodDryerBlockEntity
    }

    override fun getPlacementState(ctx: net.minecraft.item.ItemPlacementContext): BlockState? {
        val pos = ctx.blockPos
        val world = ctx.world
        // Ensure there is space for the top part
        if (!world.getBlockState(pos.up()).canReplace(ctx)) return null
        return super.getPlacementState(ctx) ?: defaultState
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val above = pos.up()
        return super.canPlaceAt(state, world, pos) && world.getBlockState(above).isAir
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient && type === ModEntityType.FOOD_DRYER_BLOCK_ENTITY) {
            BlockEntityTicker { w, pos, s, be -> 
                (be as? FoodDryerBlockEntity)?.tick(w as ServerWorld) 
            }
        } else null
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is FoodDryerBlockEntity) {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            }
        }
        return ActionResult.SUCCESS
    }

    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        if (!world.isClient) {
            val above = pos.up()
            val st = world.getBlockState(above)
            if (!st.isOf(ModBlocks.FOOD_DRYER_TOP)) {
                // Place the top half
                if (st.isAir) {
                    world.setBlockState(above, ModBlocks.FOOD_DRYER_TOP.defaultState, net.minecraft.block.Block.NOTIFY_ALL)
                }
            }
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: net.minecraft.entity.LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (!world.isClient) {
            val above = pos.up()
            if (world.getBlockState(above).isAir) {
                world.setBlockState(above, ModBlocks.FOOD_DRYER_TOP.defaultState, net.minecraft.block.Block.NOTIFY_ALL)
            }
        }
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState? {
        if (!world.isClient) {
            val above = pos.up()
            val topState = world.getBlockState(above)
            if (topState.isOf(ModBlocks.FOOD_DRYER_TOP)) {
                world.breakBlock(above, false, player)
            }
        }
        return super.onBreak(world, pos, state, player)
    }

    companion object {
        val CODEC: MapCodec<FoodDryerBlock> by lazy {
            createCodec { FoodDryerBlock(Settings.create().strength(1.5f).nonOpaque()) }
        }

        // Food dryer shape - a frame-like structure that items can hang from
        private val SHAPE: VoxelShape by lazy {
            VoxelShapes.union(
                // Base platform
                createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
                // Four corner posts
                createCuboidShape(1.0, 2.0, 1.0, 3.0, 16.0, 3.0),
                createCuboidShape(13.0, 2.0, 1.0, 15.0, 16.0, 3.0),
                createCuboidShape(1.0, 2.0, 13.0, 3.0, 16.0, 15.0),
                createCuboidShape(13.0, 2.0, 13.0, 15.0, 16.0, 15.0),
                // Cross beams for hanging
                createCuboidShape(1.0, 12.0, 3.0, 15.0, 14.0, 5.0),
                createCuboidShape(1.0, 12.0, 11.0, 15.0, 14.0, 13.0)
            )
        }
    }
}