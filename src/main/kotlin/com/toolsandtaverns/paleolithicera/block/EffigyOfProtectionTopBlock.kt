package com.toolsandtaverns.paleolithicera.block

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.entity.EffigyOfProtectionEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape

/**
 * Invisible top half of the Effigy, used for tall interaction and selection.
 * Delegates interaction to the lower block's block entity.
 */
class EffigyOfProtectionTopBlock(settings: Settings) : Block(settings) {

    override fun getCodec(): MapCodec<out Block> = CODEC

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.INVISIBLE

    override fun getOutlineShape(
        state: BlockState,
        world: net.minecraft.world.BlockView,
        pos: BlockPos,
        context: net.minecraft.block.ShapeContext
    ): VoxelShape {
        return UPPER_SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        world: net.minecraft.world.BlockView,
        pos: BlockPos,
        context: net.minecraft.block.ShapeContext
    ): VoxelShape {
        return UPPER_SHAPE
    }

    override fun onUse(
        state: BlockState,
        world: net.minecraft.world.World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        val be = world.getBlockEntity(pos.down()) as? EffigyOfProtectionEntity ?: return ActionResult.PASS

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

    override fun onBreak(world: net.minecraft.world.World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState? {
        if (!world.isClient) {
            val below = pos.down()
            val st = world.getBlockState(below)
            if (st.isOf(ModBlocks.EFFIGY_OF_PROTECTION)) {
                world.breakBlock(below, !player.shouldCancelInteraction(), player)
            }
        }
        return super.onBreak(world, pos, state, player)
    }

    companion object {
        val CODEC: MapCodec<EffigyOfProtectionTopBlock> by lazy {
            createCodec { EffigyOfProtectionTopBlock(Settings.create().nonOpaque()) }
        }

        // Upper shape (local y 0..16 maps to world y 16..32)
        // Includes upper slice of center pole (0..5) and the top (4..14)
        private val UPPER_SHAPE: VoxelShape by lazy {
            net.minecraft.util.shape.VoxelShapes.union(
                createCuboidShape(6.0, 0.0, 6.0, 10.0, 5.0, 10.0),  // center pole upper slice
                createCuboidShape(3.0, 4.0, 3.0, 13.0, 14.0, 13.0)  // top body
            )
        }
    }
}

