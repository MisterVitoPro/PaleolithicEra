package com.toolsandtaverns.paleolithicera.util

import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.block.cauldron.CauldronBehavior
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.Items
import net.minecraft.potion.Potions
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

object CauldronBehaviorRegistry {

    fun initialize() {
        val emptyMapBehavior = CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map()
        CauldronBehavior.registerBucketBehavior(emptyMapBehavior)
        emptyMapBehavior.put(
            ModItems.FILLED_WATER_SACK,
            CauldronBehavior { state: BlockState, world: World, pos: BlockPos?, player: PlayerEntity, hand: Hand, stack: ItemStack ->
                if (stack.item == ModItems.FILLED_WATER_SACK) {
                    if (!world.isClient) {
                        val item = stack.item
                        player.setStackInHand(
                            hand,
                            ItemUsage.exchangeStack(stack, player, ItemStack(ModItems.WATER_SACK))
                        )
                        player.incrementStat(Stats.USE_CAULDRON)
                        player.incrementStat(Stats.USED.getOrCreateStat(item))
                        world.setBlockState(pos, Blocks.WATER_CAULDRON.defaultState)
                        world.playSound(
                            null as Entity?,
                            pos,
                            SoundEvents.ITEM_BOTTLE_EMPTY,
                            SoundCategory.BLOCKS,
                            1.0f,
                            1.0f
                        )
                        world.emitGameEvent(null as Entity?, GameEvent.FLUID_PLACE, pos)
                    }

                    return@CauldronBehavior ActionResult.SUCCESS
                } else {
                    return@CauldronBehavior ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION
                }
            })
        val waterBehaviorMap = CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map()
        CauldronBehavior.registerBucketBehavior(waterBehaviorMap)
        waterBehaviorMap.put(
            ModItems.WATER_SACK,
            CauldronBehavior { state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, stack: ItemStack ->
                if (!world.isClient) {
                    val item = stack.item
                    player.setStackInHand(
                        hand,
                        ItemUsage.exchangeStack(
                            stack,
                            player,
                            ModItems.FILLED_WATER_SACK.defaultStack
                        )
                    )
                    player.incrementStat(Stats.USE_CAULDRON)
                    player.incrementStat(Stats.USED.getOrCreateStat(item))
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos)
                    world.playSound(
                        null as Entity?,
                        pos,
                        SoundEvents.ITEM_BOTTLE_FILL,
                        SoundCategory.BLOCKS,
                        1.0f,
                        1.0f
                    )
                    world.emitGameEvent(null as Entity?, GameEvent.FLUID_PICKUP, pos)
                }
                ActionResult.SUCCESS
            })
        waterBehaviorMap.put(
            ModItems.FILLED_WATER_SACK,
            CauldronBehavior { state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, stack: ItemStack ->
                if (state.get(LeveledCauldronBlock.LEVEL) == 3) {
                    return@CauldronBehavior ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION
                } else {
                    if (stack.item == ModItems.FILLED_WATER_SACK) {
                        if (!world.isClient) {
                            player.setStackInHand(
                                hand,
                                ItemUsage.exchangeStack(stack, player, ItemStack(ModItems.WATER_SACK))
                            )
                            player.incrementStat(Stats.USE_CAULDRON)
                            player.incrementStat(Stats.USED.getOrCreateStat(stack.item))
                            world.setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL))
                            world.playSound(
                                null as Entity?,
                                pos,
                                SoundEvents.ITEM_BOTTLE_EMPTY,
                                SoundCategory.BLOCKS,
                                1.0f,
                                1.0f
                            )
                            world.emitGameEvent(null as Entity?, GameEvent.FLUID_PLACE, pos)
                        }

                        return@CauldronBehavior ActionResult.SUCCESS
                    } else {
                        return@CauldronBehavior ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION
                    }
                }
            })
    }

}