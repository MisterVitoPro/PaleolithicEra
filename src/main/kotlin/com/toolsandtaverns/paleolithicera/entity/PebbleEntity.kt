package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModParticleTypes
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
import net.minecraft.util.math.Vec3d

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
            
            // Spawn impact particles for entity hits
            spawnImpactParticles(serverWorld, this.pos, entity.velocity)
        }

        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, 4.toByte()) // Use different status for entity hits
            this.discard()
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        
        // Play hit sound
        this.playSound(SoundEvents.BLOCK_STONE_HIT, 0.5f, 1.0f)
        
        if (!this.world.isClient) {
            val serverWorld = this.world as ServerWorld
            val hitPos = blockHitResult.pos
            val hitNormal = Vec3d.of(blockHitResult.side.vector)
            
            // Spawn impact particles for block hits
            spawnBlockImpactParticles(serverWorld, hitPos, hitNormal)
            
            this.world.sendEntityStatus(this, 3.toByte()) // Keep original status for block hits
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
     * Called when status is received to spawn particles on client side
     */
    override fun handleStatus(status: Byte) {
        when (status) {
            3.toByte() -> {
                // Block hit particles - spawn both impact and dust particles
                spawnClientBlockImpactParticles()
            }
            4.toByte() -> {
                // Entity hit particles - spawn only impact particles (no dust for flesh hits)
                spawnClientEntityImpactParticles()
            }
        }
    }
    
    /**
     * Spawns impact particles on the server for block hits.
     * Creates both debris and dust particles with physics appropriate for stone impacts.
     */
    private fun spawnBlockImpactParticles(world: ServerWorld, hitPos: Vec3d, normal: Vec3d) {
        val particleCount = 8 + world.random.nextInt(8) // 8-15 particles
        val dustCount = 12 + world.random.nextInt(12) // 12-23 dust particles
        
        // Spawn main impact particles
        world.spawnParticles(
            ModParticleTypes.PEBBLE_IMPACT,
            hitPos.x, hitPos.y, hitPos.z,
            particleCount,
            0.1, 0.1, 0.1, // Spread
            0.15 // Speed
        )
        
        // Spawn dust particles
        world.spawnParticles(
            ModParticleTypes.STONE_DUST,
            hitPos.x + normal.x * 0.1, 
            hitPos.y + normal.y * 0.1, 
            hitPos.z + normal.z * 0.1,
            dustCount,
            0.2, 0.2, 0.2, // More spread for dust
            0.05 // Lower speed for dust
        )
    }
    
    /**
     * Spawns impact particles on the server for entity hits.
     * Creates only debris particles (no dust for biological targets).
     */
    private fun spawnImpactParticles(world: ServerWorld, pos: Vec3d, targetVelocity: Vec3d) {
        val particleCount = 4 + world.random.nextInt(4) // 4-7 particles
        
        // Spawn fewer particles for entity hits
        world.spawnParticles(
            ModParticleTypes.PEBBLE_IMPACT,
            pos.x, pos.y, pos.z,
            particleCount,
            0.1, 0.1, 0.1, // Spread
            0.1 // Lower speed for entity hits
        )
    }
    
    /**
     * Client-side particle spawning for block impacts.
     * The actual client-side particle rendering is handled through the status system.
     */
    private fun spawnClientBlockImpactParticles() {
        // Client-side particle spawning is handled by the particle system
        // through the handleStatus method when status 3 is received
        // This method is called from handleStatus for client-side effects
    }
    
    /**
     * Client-side particle spawning for entity impacts.
     * The actual client-side particle rendering is handled through the status system.
     */
    private fun spawnClientEntityImpactParticles() {
        // Client-side particle spawning is handled by the particle system
        // through the handleStatus method when status 4 is received
        // This method is called from handleStatus for client-side effects
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