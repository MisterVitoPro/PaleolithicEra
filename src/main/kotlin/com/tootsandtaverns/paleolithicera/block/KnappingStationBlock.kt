package com.tootsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import com.tootsandtaverns.paleolithicera.PaleolithicEra.logger
import com.tootsandtaverns.paleolithicera.block.entity.KnappingStationBlockEntity
import com.tootsandtaverns.paleolithicera.registry.ModBlockEntities
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
import org.slf4j.LoggerFactory

class KnappingStationBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        KnappingStationBlockEntity(pos, state)

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos)
        return blockEntity as? KnappingStationBlockEntity
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return validateTicker(type, ModBlockEntities.KNAPPING_STATION) { w, p, s, be ->
            if (be is KnappingStationBlockEntity) be.tick()
        }
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
            createCodec { KnappingStationBlock(AbstractBlock.Settings.create().strength(2.0f)) }
        }
    }
}
