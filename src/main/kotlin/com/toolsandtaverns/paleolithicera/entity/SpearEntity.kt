package com.toolsandtaverns.paleolithicera.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundEvent
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

/**
 * Base projectile for Paleolithic spears that adds a brief Slowness debuff
 * to ANIMALS on hit. Subclasses only need to:
 *  - Provide default spear item via getDefaultItemStack()
 *  - Optionally override slownessTickDuration / slownessAmplifier
 */
abstract class SpearEntity : PersistentProjectileEntity {

    /**
     * Constructor used by subclass two-arg convenience constructor.
     */
    constructor(type: EntityType<out SpearEntity>, world: World) : super(type, world)

    /**
     * Constructor for thrown spears with an owner + stack.
     * Subclasses can expose a public convenience ctor that calls this via super(...).
     */
    constructor(
        type: EntityType<out SpearEntity>,
        owner: LivingEntity,
        world: World,
        stack: ItemStack
    ) : super(type, owner, world, stack, stack) {
        this.setNoGravity(false)
        // Calculate damage from the item's attributes
        val totalDamage = calculateDamageFromStack(stack, owner)
        this.setDamage(totalDamage)
    }

    // --- Configuration knobs (override per spear material/tier) ---

    /** Base damage dealt by this spear type. */
    protected open val baseDamage: Float = 2.0f

    /** How long the Slowness lasts, in ticks (20 ticks = 1s). */
    protected open val slownessTickDuration: Int = 60 // 3s

    /** Slowness amplifier: 0 = Slowness I, 1 = II, etc. */
    protected open val slownessAmplifier: Int = 0

    /** Whether to only slow animals (true) or any LivingEntity (false). */
    protected open val animalsOnly: Boolean = true

    /** Whether to discard the projectile after a living-entity hit. */
    protected open val discardOnHit: Boolean = true

    /**
     * Called when the projectile collides with an entity.
     * Applies Slowness to valid targets, then performs default cleanup.
     */
    override fun onEntityHit(hitResult: EntityHitResult) {
        super.onEntityHit(hitResult)

        val target = hitResult.entity
        if (target is LivingEntity) {
            val isValid = if (animalsOnly) target is AnimalEntity else true
            if (isValid && !world.isClient) {
                // Apply Slowness
                target.addStatusEffect(
                    StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        slownessTickDuration,
                        slownessAmplifier
                    )
                )

                // Small hit feedback on server clients
                // (purely visual; safe to do on server via tracked events/packets)
                (world as? ServerWorld)?.spawnParticles(
                    ParticleTypes.POOF,
                    target.x, target.getBodyY(0.5), target.z,
                    4, 0.1, 0.1, 0.1, 0.01
                )
            }

            if (discardOnHit && !world.isClient) {
                this.discard()
            }
        }
    }

    /**
     * Subclasses MUST return the associated spear item so pickups render correctly.
     */
    abstract override fun getDefaultItemStack(): ItemStack

    /**
     * Optionally override to customize impact sounds per material.
     */
    override fun getHitSound(): SoundEvent? = SoundEvents.BLOCK_WOOD_HIT

    /**
     * Always render; early-game spears are meant to be visible in-flight.
     */
    override fun shouldRender(cameraX: Double, cameraY: Double, cameraZ: Double): Boolean = true

    /**
     * Calculates the total damage this spear should deal based on the item's attributes.
     * This should match the melee damage of the item to maintain balance.
     * 
     * Note: PersistentProjectileEntity applies velocity-based damage multipliers,
     * so we need to account for that to match melee damage exactly.
     */
    private fun calculateDamageFromStack(stack: ItemStack, owner: LivingEntity): Double {
        // Calculate damage to match the melee attack damage of the spear item
        // SpearItem uses: material.attackDamageBonus + 1.5
        // But PersistentProjectileEntity seems to apply ~3x multiplier based on velocity
        // So we need to divide by ~3 to get the correct final damage
        
        if (stack.item is com.toolsandtaverns.paleolithicera.item.SpearItem) {
            // Target damage: Wooden = 1.5, Bone = 2.5
            // Reduce by factor of ~3 to compensate for velocity multiplier
            return when (stack.item) {
                com.toolsandtaverns.paleolithicera.registry.ModItems.WOODEN_SPEAR -> 0.5  // Should result in ~1.5 final damage
                com.toolsandtaverns.paleolithicera.registry.ModItems.BONE_SPEAR -> 0.85   // Should result in ~2.5 final damage  
                else -> baseDamage.toDouble() / 3.0
            }
        }
        
        return baseDamage.toDouble()
    }
}
