package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.GroundStorageBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * GroundStorageBlock is a simple dirt-based container block that provides
 * 8 slots for storing stackable items. It uses a BlockEntity to persist
 * inventory and opens a custom screen when used.
 */
class GroundStorageBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun getCodec(): MapCodec<out BlockWithEntity?> = CODEC

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        GroundStorageBlockEntity(pos, state)

    //? if <=1.21.4 {
    /*override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (!state.isOf(newState.block)) {
            (world.getBlockEntity(pos) as? GroundStorageBlockEntity)?.dropItems(world, pos)
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }*///?}

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory? {
        val be = world.getBlockEntity(pos)
        return be as? GroundStorageBlockEntity
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            val be = world.getBlockEntity(pos)
            if (be is GroundStorageBlockEntity) {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            }
        }
        return ActionResult.SUCCESS
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        // No ticking behavior required for ground storage
        return null
    }

    companion object {
        val CODEC: MapCodec<GroundStorageBlock> by lazy {
            createCodec { GroundStorageBlock(Settings.create().strength(0.5f)) }
        }
    }
}
