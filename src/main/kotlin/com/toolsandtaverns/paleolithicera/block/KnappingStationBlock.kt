package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.block.entity.KnappingStationBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class KnappingStationBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        KnappingStationBlockEntity(pos, state)

    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        return blockEntity as KnappingStationBlockEntity
    }

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

    override fun getCodec(): MapCodec<out BlockWithEntity> = CODEC

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            val factory = state.createScreenHandlerFactory(world, pos)
            if (factory != null) {
                player.openHandledScreen(factory)
            }
        }
        return ActionResult.SUCCESS
    }


    companion object {
        val CODEC: MapCodec<KnappingStationBlock> by lazy {
            createCodec { KnappingStationBlock(Settings.create().strength(2.0f)) }
        }
    }
}
