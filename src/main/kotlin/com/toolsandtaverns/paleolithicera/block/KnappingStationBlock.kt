package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.block.entity.KnappingStationBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModEntities
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.server.world.ServerWorld
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
        println("onUse() Triggered")
        val blockEntity = world.getBlockEntity(pos) as? KnappingStationBlockEntity ?: return ActionResult.PASS

        return if (player.isSneaking) {
            if (!world.isClient) {
                blockEntity.knap(world)
            }
            ActionResult.SUCCESS
        } else {
            if (!world.isClient) {
                player.openHandledScreen(blockEntity)
            }
            ActionResult.SUCCESS
        }
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient && type === ModEntities.KNAPPING_STATION) {
            BlockEntityTicker { w, pos, s, be ->
                (be as? KnappingStationBlockEntity)?.tick(w as ServerWorld)
            }
        } else {
            null
        }
    }

    companion object {
        val CODEC: MapCodec<KnappingStationBlock> by lazy {
            createCodec { KnappingStationBlock(Settings.create().strength(2.0f)) }
        }
    }
}
