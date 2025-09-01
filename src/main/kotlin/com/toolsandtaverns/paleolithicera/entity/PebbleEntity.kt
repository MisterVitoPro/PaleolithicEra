package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

/**
 * Projectile entity for pebbles thrown by slings.
 * Simple projectile that deals minor damage and breaks on impact.
 */
class PebbleEntity : ThrownItemEntity {

    companion object {
        private const val DAMAGE = 2.0f
    }

    constructor(entityType: EntityType<out ThrownItemEntity>, world: World) : super(entityType, world)

    constructor(world: World, owner: LivingEntity) : super(ModEntityType.PEBBLE_ENTITY, owner, world, ItemStack(ModItems.PEBBLE))

    constructor(world: World, x: Double, y: Double, z: Double) : super(ModEntityType.PEBBLE_ENTITY, x, y, z, world, ItemStack(ModItems.PEBBLE))

    override fun getDefaultItem(): Item = ModItems.PEBBLE

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        
        val entity = entityHitResult.entity
        if (entity is LivingEntity && !this.world.isClient) {
            val serverWorld = this.world as ServerWorld
            
            // Create damage source from the owner
            val ownerEntity = this.getOwner()
            val damageSource = if (ownerEntity != null) {
                this.damageSources.thrown(this, ownerEntity)
            } else {
                this.damageSources.thrown(this, this)
            }
            
            // Deal damage to the entity
            entity.damage(serverWorld, damageSource, DAMAGE)
            
            // Play hit sound
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0f, 1.2f)
        }

        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, 3.toByte())
            this.discard()
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        
        // Play hit sound
        this.playSound(SoundEvents.BLOCK_STONE_HIT, 0.5f, 1.0f)
        
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, 3.toByte())
            this.discard()
        }
    }

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        
        if (!this.world.isClient) {
            // Create particle effect on impact
            this.world.sendEntityStatus(this, 3.toByte())
        }
    }

    /**
     * Called when status 3 is received to spawn particles
     */
    override fun handleStatus(status: Byte) {
        if (status == 3.toByte()) {
            val particleEffect = ItemStackParticleEffect(ParticleTypes.ITEM, this.stack)
            for (i in 0..3) {
                val serverWorld = world as? ServerWorld
                if (serverWorld != null) {
                    serverWorld.spawnParticles(
                        particleEffect,
                        this.x,
                        this.y,
                        this.z,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                    )
                }
            }
        }
    }

    /**
     * Override to initialize data tracker
     */
    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
    }

    override fun getGravity(): Double {
        return 0.03 // Same as snowball/eggs
    }
}