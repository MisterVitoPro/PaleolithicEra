package com.toolsandtaverns.paleolithicera.item

import com.google.common.collect.BiMap
import com.google.common.collect.ImmutableMap
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.*
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import java.util.*
import java.util.function.Function

/**
 * Knife item that can strip logs to drop bark.
 * Copied from AxeItem but did not want knives to cut trees
 */
class KnifeItem(
    material: ToolMaterial,
    settings: Settings,
    attackDamage: Float = 0.5f,
    attackSpeed: Float = -2.2f,
) : Item(settings.tool(material, BlockTags.WOOL, attackDamage, attackSpeed, 0.0F)) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val blockPos = context.blockPos
        val playerEntity: PlayerEntity? = context.player
        if (shouldCancelStripAttempt(context)) {
            return ActionResult.PASS
        } else {
            val optional: Optional<BlockState> =
                this.tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos))
            if (optional.isEmpty) {
                return ActionResult.PASS
            } else {
                val itemStack = context.stack
                if (playerEntity is ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(playerEntity, blockPos, itemStack)
                }

                world.setBlockState(blockPos, optional.get(), Block.NOTIFY_ALL_AND_REDRAW)
                world.emitGameEvent(
                    GameEvent.BLOCK_CHANGE,
                    blockPos,
                    GameEvent.Emitter.of(playerEntity, optional.get())
                )
                if (playerEntity != null) {
                    itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.hand))
                }

                Block.dropStack(context.world, context.blockPos, ItemStack(ModItems.BARK))

                return ActionResult.SUCCESS
            }
        }
    }

    override fun getMiningSpeed(stack: ItemStack, state: BlockState): Float {
        return if (state.block is LeavesBlock) {
            5.0f // Adjust this value for speed. Vanilla shears are 15.0f
        } else {
            super.getMiningSpeed(stack, state)
        }
    }

    private fun shouldCancelStripAttempt(context: ItemUsageContext): Boolean {
        val playerEntity: PlayerEntity? = context.player
        return context.hand == Hand.MAIN_HAND && playerEntity?.offHandStack!!
            .contains(DataComponentTypes.BLOCKS_ATTACKS) && !playerEntity.shouldCancelInteraction()
    }

    private fun tryStrip(
        world: World,
        pos: BlockPos,
        player: PlayerEntity?,
        state: BlockState
    ): Optional<BlockState> {
        val optional: Optional<BlockState> = this.getStrippedState(state)
        if (optional.isPresent) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f)
            return optional
        } else {
            val optional2 = Oxidizable.getDecreasedOxidationState(state)
            if (optional2.isPresent) {
                world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, pos, 0)
                return optional2
            } else {
                val unwaxed = (HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get() as BiMap<*, *>)[state.block] as? Block
                return if (unwaxed != null) {
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, pos, 0)
                    Optional.of(unwaxed.getStateWithProperties(state))
                } else {
                    Optional.empty()
                }
            }
        }
    }

    private fun getStrippedState(state: BlockState): Optional<BlockState> {
        return Optional.ofNullable<Block>(STRIPPED_BLOCKS.get(state.block))
            .map { block: Block -> block.defaultState.with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)) }
    }

    companion object {
        val STRIPPED_BLOCKS: ImmutableMap<Block, Block> = (ImmutableMap.Builder<Block, Block>()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD).put(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build()
    }
}
