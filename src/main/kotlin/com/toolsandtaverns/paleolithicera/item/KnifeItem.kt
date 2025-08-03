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
 * Knife item that represents a primitive cutting tool used for stripping logs to obtain bark.
 * 
 * The knife is a fundamental Paleolithic tool that serves multiple purposes in early survival:
 * - Stripping logs to obtain bark (a critical early-game resource)
 * - Harvesting leaves more efficiently than by hand
 * - Light combat capabilities, though inferior to dedicated weapons
 *
 * Unlike the vanilla axe, the knife is designed specifically as a primitive technology item that
 * balances usefulness with appropriate limitations for the Paleolithic era progression system.
 * It cannot efficiently harvest trees but excels at targeted material gathering from them.
 *
 * @param material The tool material determining durability and mining level
 * @param settings Item settings that can be customized
 * @param attackDamage Base attack damage value (intentionally low for primitive tools)
 * @param attackSpeed Attack speed modifier (slightly slower than hand)
 */
class KnifeItem(
    material: ToolMaterial,
    settings: Settings,
    attackDamage: Float = 0.5f,
    attackSpeed: Float = -2.2f,
) : Item(settings.tool(material, BlockTags.WOOL, attackDamage, attackSpeed, 0.0F)) {

    /**
     * Handles the behavior when the knife is used on a block, primarily for stripping logs.
     * 
     * This method represents the bark-gathering technique used by Paleolithic humans,
     * where primitive tools were used to extract useful materials from trees without
     * necessarily felling them. When a player uses the knife on a log:
     * 
     * 1. It checks if the attempt should be canceled (e.g., blocked by shield)
     * 2. Attempts to strip the log block, converting it to its stripped variant
     * 3. Triggers advancement criteria for using items on blocks
     * 4. Updates the block state in the world and notifies clients
     * 5. Damages the knife tool (representing wear from use)
     * 6. Drops bark as a resource (a critical early-game crafting material)
     *
     * This creates an important early-game gathering loop where players must collect
     * bark using knives to progress in crafting and technology.
     *
     * @param context The context of the item usage
     * @return SUCCESS if the block was stripped, PASS otherwise
     */
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

    /**
     * Determines the mining speed of the knife against different block types.
     * 
     * The knife has a special interaction with leaves, allowing faster collection
     * than by hand but not as fast as dedicated tools like shears. This represents
     * how Paleolithic humans would have used cutting tools to gather leafy materials
     * for various purposes like bedding, kindling, or primitive roofing materials.
     * 
     * The intentionally modest speed (5.0f vs. shears' 15.0f) reflects the technological
     * limitations of primitive stone tools while still providing gameplay incentive
     * to craft and use knives for specific gathering tasks.
     *
     * @param stack The knife ItemStack being used
     * @param state The BlockState being mined
     * @return The mining speed multiplier
     */
    override fun getMiningSpeed(stack: ItemStack, state: BlockState): Float {
        return if (state.block is LeavesBlock) {
            5.0f // speed -- Vanilla shears are 15.0f
        } else {
            super.getMiningSpeed(stack, state)
        }
    }

    /**
     * Determines if a stripping attempt should be canceled due to shield interactions.
     * 
     * This method prevents accidental bark harvesting when the player is using a shield
     * in their off-hand and might be attempting to block rather than strip. This gameplay
     * consideration helps improve the user experience by preventing unintended tool use.
     *
     * @param context The context of the item usage
     * @return true if the stripping attempt should be canceled, false otherwise
     */
    private fun shouldCancelStripAttempt(context: ItemUsageContext): Boolean {
        val playerEntity: PlayerEntity? = context.player
        return context.hand == Hand.MAIN_HAND && playerEntity?.offHandStack!!
            .contains(DataComponentTypes.BLOCKS_ATTACKS) && !playerEntity.shouldCancelInteraction()
    }

    /**
     * Attempts to strip, scrape, or unwax a block using the knife.
     * 
     * While primarily used for stripping logs to gather bark, this method also supports
     * other vanilla interactions like scraping oxidized copper or removing wax. These
     * additional functions represent how primitive cutting tools would have been
     * multipurpose implements, used for various precise material manipulation tasks.
     * 
     * The method follows a priority order:
     * 1. Try to strip logs (primary Paleolithic use case)
     * 2. Try to scrape oxidation (advanced use case)
     * 3. Try to remove wax (advanced use case)
     * 
     * Appropriate sounds and visual effects are played based on the action performed.
     *
     * @param world The world containing the block
     * @param pos The position of the block
     * @param player The player using the knife
     * @param state The current state of the block
     * @return An Optional containing the new BlockState if successful, empty otherwise
     */
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

    /**
     * Gets the stripped variant of a log block state.
     * 
     * This helper method determines what a log block will become when stripped
     * by the knife, preserving properties like axis orientation. The stripping
     * mechanism is central to the bark gathering system in the Paleolithic era mod,
     * where bark is a critical early-game resource for crafting.
     *
     * @param state The current block state
     * @return An Optional containing the stripped block state if applicable, empty otherwise
     */
    private fun getStrippedState(state: BlockState): Optional<BlockState> {
        return Optional.ofNullable<Block>(STRIPPED_BLOCKS.get(state.block))
            .map { block: Block -> block.defaultState.with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)) }
    }

    companion object {
        /**
         * A mapping of normal log/wood blocks to their stripped variants.
         * 
         * This comprehensive mapping enables the knife to strip any vanilla log or wood type,
         * producing its stripped variant. The extensive support for different wood types reflects
         * how Paleolithic humans would have utilized whatever timber was available in their
         * environment, adapting their tool usage to local resources.
         * 
         * Each entry represents a strippable block and what it becomes when stripped by the knife,
         * which is a critical part of the bark gathering gameplay loop in early progression.
         */
        val STRIPPED_BLOCKS: ImmutableMap<Block, Block> = (ImmutableMap.Builder<Block, Block>()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD).put(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build()
    }
}
