package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.HideDryerBlockEntity
import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.screen.HideDryerScreenHandler
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Consumer

/**
 * HideDryerBlock is a custom block that lets players dry Rawhide into Dry Rawhide during the day.
 * It uses a BlockEntity to handle inventory and tick-based drying logic.
 */
class HideDryerBlock(settings: Settings) : BlockWithEntity(settings)  {

    override fun getCodec(): MapCodec<out BlockWithEntity?> = CODEC

    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        return blockEntity as HideDryerBlockEntity
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return HideDryerBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient && type === ModEntities.HIDE_DRYER_BLOCK_ENTITY) {
            BlockEntityTicker { w, pos, s, be ->
                (be as? HideDryerBlockEntity)?.tick(w as ServerWorld)
            }
        } else {
            null
        }
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
            if (blockEntity is HideDryerBlockEntity) {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
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
