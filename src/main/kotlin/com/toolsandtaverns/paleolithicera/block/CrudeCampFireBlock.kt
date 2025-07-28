package com.toolsandtaverns.paleolithicera.block

import com.toolsandtaverns.paleolithicera.block.entity.CrudeCampfireBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModEntities
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.ServerRecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stats
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * A primitive version of the vanilla campfire that must be lit using a Fire Drill.
 * Crafted unlit and doesn't automatically light or use coal-based recipes.
 */
class CrudeCampFireBlock(settings: Settings) : CampfireBlock(false, 1, settings) {

    init {
        defaultState = stateManager.defaultState
            .with(Properties.LIT, false)
            .with(Properties.SIGNAL_FIRE, false)
            .with(Properties.WATERLOGGED, false)
            .with(FACING, Direction.NORTH)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CrudeCampfireBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T?>?
    ): BlockEntityTicker<T?>? {
        return when {
            world.isClient -> {
                validateTicker(
                    type,
                    ModEntities.CRUDE_CAMPFIRE,
                    BlockEntityTicker { w, pos, s, be ->
                        CrudeCampfireBlockEntity.clientTick(w, pos, s, be)
                    }
                )
            }

            state.get(Properties.LIT) -> {
                val matchGetter = ServerRecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING)
                validateTicker(
                    type,
                    ModEntities.CRUDE_CAMPFIRE,
                    BlockEntityTicker { w, pos, s, be ->
                        CrudeCampfireBlockEntity.litServerTick(w as ServerWorld, pos, s, be, matchGetter)
                    }
                )
            }

            else -> {
                validateTicker(
                    type,
                    ModEntities.CRUDE_CAMPFIRE,
                    BlockEntityTicker { w, pos, s, be ->
                        CrudeCampfireBlockEntity.unlitServerTick(w, pos, s, be)
                    }
                )
            }
        }
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        // Make sure it's placed unlit by default
        world.setBlockState(pos, state.with(Properties.LIT, false), 3)
    }

    override fun onUseWithItem(
        stack: ItemStack,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!state.get(Properties.LIT)) {
            return ActionResult.PASS
        }

        val blockEntity = world.getBlockEntity(pos)

        if (blockEntity is CrudeCampfireBlockEntity && world is ServerWorld) {
            val recipeSet = world.recipeManager.getPropertySet(net.minecraft.recipe.RecipePropertySet.CAMPFIRE_INPUT)

            if (recipeSet.canUse(stack)) {
                if (blockEntity.addItem(world, player, stack)) {
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE)
                    if (!player.abilities.creativeMode) {
                        stack.decrement(1)
                    }
                    return ActionResult.SUCCESS
                }
                return ActionResult.CONSUME
            }
        }

        return ActionResult.PASS
    }
}
