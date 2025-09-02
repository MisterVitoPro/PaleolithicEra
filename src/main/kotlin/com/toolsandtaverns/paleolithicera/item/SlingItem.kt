package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.entity.PebbleEntity
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ProjectileItem
import net.minecraft.item.consume.UseAction
import net.minecraft.server.world.ServerWorld
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Position
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import kotlin.math.cos
import kotlin.math.sin
import com.toolsandtaverns.paleolithicera.event.HotbarReplacementHandler

/**
 * Sling weapon item that uses pebbles as ammunition.
 * Functions similar to a bow but with swinging animation and limited durability.
 */
class SlingItem(settings: Settings) : Item(settings), ProjectileItem {

    companion object {
        private const val MAX_USE_TIME = 72000
        private const val MIN_CHARGE_TIME = 10
        private const val MAX_VELOCITY = 3.0f
        private const val ACCURACY_VARIANCE = 0.02f // Small variance for slight inaccuracy
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BOW // We'll use BOW animation for now, can be changed to custom later
    }

    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return MAX_USE_TIME
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        val itemStack = user.getStackInHand(hand)
        
        // Check if player has pebbles
        if (!hasPebbles(user) && !user.isInCreativeMode) {
            return ActionResult.FAIL
        }
        
        user.setCurrentHand(hand)
        return ActionResult.CONSUME
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int): Boolean {
        if (user !is PlayerEntity) return false

        val chargeTime = getMaxUseTime(stack, user) - remainingUseTicks
        if (chargeTime < MIN_CHARGE_TIME) return false

        // Check for pebbles and consume one
        if (!user.isInCreativeMode) {
            if (!consumePebble(user)) return false
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this))
        
        if (world is ServerWorld) {
            // Calculate velocity based on charge time
            val velocity = calculateVelocity(chargeTime)
            
            // Create and launch pebble using the proper constructor
            val pebbleEntity = PebbleEntity(world, user)
            
            // Position the pebble at the user's eye level
            pebbleEntity.setPosition(user.x, user.getEyeY() - 0.1, user.z)
            
            // Add slight inaccuracy
            val random = world.random
            val yawOffset = (random.nextFloat() - 0.5f) * ACCURACY_VARIANCE * 180f
            val pitchOffset = (random.nextFloat() - 0.5f) * ACCURACY_VARIANCE * 180f
            
            // Set velocity using the same method as arrows
            pebbleEntity.setVelocity(
                user, 
                user.pitch + pitchOffset, 
                user.yaw + yawOffset, 
                0.0f, 
                velocity, 
                1.0f
            )
            
            // Spawn the entity
            world.spawnEntity(pebbleEntity)
            
            // Play sound
            world.playSound(
                null, 
                user.x, user.y, user.z,
                SoundEvents.ENTITY_SNOWBALL_THROW, 
                SoundCategory.PLAYERS, 
                0.5f, 
                0.4f / (random.nextFloat() * 0.4f + 0.8f)
            )
            
            // Damage the sling AFTER successful use - this ensures durability is consumed
            // even if the item breaks, preventing infinite usage at low durability
            // Find which hand is holding the sling
            val hand = if (user.mainHandStack == stack) Hand.MAIN_HAND else Hand.OFF_HAND
            val originalStack = stack.copy()
            val slotIndex = if (hand == Hand.MAIN_HAND) user.inventory.selectedSlot else 40
            
            stack.damage(1, user, LivingEntity.getSlotForHand(hand))
            
            // If the sling broke, trigger replacement
            if (stack.isEmpty) {
                HotbarReplacementHandler.checkAndReplaceItem(user as ServerPlayerEntity, slotIndex, originalStack)
            }
            
            return true
        }
        
        return false
    }

    private fun hasPebbles(player: PlayerEntity): Boolean {
        return player.inventory.contains(ModItems.PEBBLE.defaultStack)
    }

    private fun consumePebble(player: PlayerEntity): Boolean {
        for (i in 0 until player.inventory.size()) {
            val stack = player.inventory.getStack(i)
            if (stack.item == ModItems.PEBBLE) {
                stack.decrement(1)
                return true
            }
        }
        return false
    }

    private fun calculateVelocity(chargeTime: Int): Float {
        var velocity = chargeTime.toFloat() / 20.0f
        velocity = (velocity * velocity + velocity * 2.0f) / 3.0f
        if (velocity > 1.0f) velocity = 1.0f
        return velocity * MAX_VELOCITY
    }

    override fun createEntity(world: World, pos: Position, stack: ItemStack, direction: Direction?): ProjectileEntity {
        if (world !is ServerWorld) {
            throw IllegalStateException("Projectile can only be created on the server side")
        }
        
        val entity = PebbleEntity(ModEntityType.PEBBLE_ENTITY, world)
        entity.setPosition(pos.x, pos.y, pos.z)
        return entity
    }
}