package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Blocks
import net.minecraft.block.CauldronBlock
import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.item.consume.UseAction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import net.minecraft.entity.Entity
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Direction

class FilledWaterSackItem(settings: Settings) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val pos = context.blockPos
        val player = context.player ?: return ActionResult.PASS
        val itemStack = context.stack
        val blockState = world.getBlockState(pos)

        // Create mud from dirt/convertible blocks - matches vanilla water bottle behavior
        if (context.side != Direction.DOWN && blockState.isIn(BlockTags.CONVERTABLE_TO_MUD)) {
            world.playSound(null as Entity?, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0f, 1.0f)
            player.setStackInHand(context.hand, ItemUsage.exchangeStack(itemStack, player, ItemStack(ModItems.WATER_SACK)))
            player.incrementStat(Stats.USED.getOrCreateStat(itemStack.item))
            if (!world.isClient) {
                val serverWorld = world as ServerWorld
                
                for (i in 0 until 5) {
                    serverWorld.spawnParticles(
                        ParticleTypes.SPLASH,
                        pos.x.toDouble() + world.random.nextDouble(),
                        (pos.y + 1).toDouble(),
                        pos.z.toDouble() + world.random.nextDouble(),
                        1, 0.0, 0.0, 0.0, 1.0
                    )
                }
            }
            
            world.playSound(null as Entity?, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f)
            world.emitGameEvent(null as Entity?, GameEvent.FLUID_PLACE, pos)
            world.setBlockState(pos, Blocks.MUD.defaultState)
            return ActionResult.SUCCESS
        }

        // Fill cauldrons - matches vanilla water bottle behavior
        when (blockState.block) {
            is CauldronBlock -> {
                // Empty cauldron - convert to water cauldron with level 1
                if (!world.isClient) {
                    player.incrementStat(Stats.USE_CAULDRON)
                    world.setBlockState(pos, Blocks.WATER_CAULDRON.defaultState.with(LeveledCauldronBlock.LEVEL, 1))
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos)
                    return ActionResult.SUCCESS.withNewHandStack(empty(itemStack, player, ItemStack(ModItems.WATER_SACK)))
                }
                return ActionResult.SUCCESS
            }
            is LeveledCauldronBlock -> {
                val currentLevel = blockState.get(LeveledCauldronBlock.LEVEL)
                
                // Can fill if cauldron is not full (less than 3 levels)
                if (currentLevel < 3) {
                    if (!world.isClient) {
                        player.incrementStat(Stats.USE_CAULDRON)
                        val newLevel = currentLevel + 1
                        world.setBlockState(pos, blockState.with(LeveledCauldronBlock.LEVEL, newLevel))
                        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f)
                        world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos)
                        return ActionResult.SUCCESS.withNewHandStack(empty(itemStack, player, ItemStack(ModItems.WATER_SACK)))
                    }
                    return ActionResult.SUCCESS
                }
            }
        }

        return ActionResult.PASS
    }

    protected fun empty(stack: ItemStack, player: PlayerEntity, outputStack: ItemStack): ItemStack {
        player.incrementStat(Stats.USED.getOrCreateStat(this))
        return ItemUsage.exchangeStack(stack, player, outputStack)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        user.setCurrentHand(hand)
        return ActionResult.SUCCESS
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        if (!world.isClient) {
            if (user is PlayerEntity) {
                user.incrementStat(Stats.USED.getOrCreateStat(this))
                
                if (user is ServerPlayerEntity) {
                    Criteria.CONSUME_ITEM.trigger(user, stack)
                }
            }
        }
        
        // Return empty water sack after drinking (like water bottles return glass bottles)
        return ItemStack(ModItems.WATER_SACK)
    }

    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return 32 // Same as vanilla potions/food
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.DRINK
    }
}
