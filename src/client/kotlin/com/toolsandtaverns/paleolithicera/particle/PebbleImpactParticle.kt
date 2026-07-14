package com.toolsandtaverns.paleolithicera.particle

import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.MathHelper

/**
 * Particle effect for pebble projectile impacts.
 * 
 * This particle creates small debris effects when pebbles hit surfaces,
 * with physics appropriate for stone fragments and dust. The particles
 * have a brown/gray color scheme to match stone materials and provide
 * visual feedback for successful hits.
 */
class PebbleImpactParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double
) : SpriteBillboardParticle(world, x, y, z, velocityX, velocityY, velocityZ) {

    private val initialScale: Float
    private val initialAlpha: Float

    companion object {
        // Color range for stone debris - browns and grays
        private const val MIN_RED = 0.4f
        private const val MAX_RED = 0.7f
        private const val MIN_GREEN = 0.3f
        private const val MAX_GREEN = 0.5f
        private const val MIN_BLUE = 0.2f
        private const val MAX_BLUE = 0.4f
        
        // Particle physics constants
        private const val MIN_LIFETIME = 10
        private const val MAX_LIFETIME = 30
        private const val GRAVITY_MODIFIER = 0.8f
        private const val AIR_RESISTANCE = 0.98f
        private const val MIN_SCALE = 0.1f
        private const val MAX_SCALE = 0.3f
    }

    init {
        // Set random lifetime between min and max
        this.maxAge = MIN_LIFETIME + world.random.nextInt(MAX_LIFETIME - MIN_LIFETIME)
        
        // Set random scale within appropriate range for debris
        this.scale = MIN_SCALE + world.random.nextFloat() * (MAX_SCALE - MIN_SCALE)
        
        // Set stone-like color with some variation
        this.red = MIN_RED + world.random.nextFloat() * (MAX_RED - MIN_RED)
        this.green = MIN_GREEN + world.random.nextFloat() * (MAX_GREEN - MIN_GREEN)
        this.blue = MIN_BLUE + world.random.nextFloat() * (MAX_BLUE - MIN_BLUE)
        
        // Set initial alpha with slight transparency
        this.alpha = 0.8f + world.random.nextFloat() * 0.2f
        initialScale = scale
        initialAlpha = alpha
        
        // Apply gravity to make particles fall naturally
        this.gravityStrength = GRAVITY_MODIFIER
        
        // Reduce initial velocity slightly for more realistic debris
        this.velocityX = velocityX * 0.3
        this.velocityY = velocityY * 0.3
        this.velocityZ = velocityZ * 0.3
    }

    override fun getType(): ParticleTextureSheet {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun tick() {
        super.tick()
        
        // Apply air resistance
        this.velocityX *= AIR_RESISTANCE
        this.velocityY *= AIR_RESISTANCE
        this.velocityZ *= AIR_RESISTANCE
        
        // Fade out over lifetime
        val lifeProgress = this.age.toFloat() / this.maxAge.toFloat()
        this.alpha = MathHelper.lerp(lifeProgress, initialAlpha, 0.0f)
        
        // Slightly shrink particles over time
        this.scale = MathHelper.lerp(lifeProgress, initialScale, initialScale * 0.5f)
    }

    /**
     * Factory for creating PebbleImpactParticle instances.
     */
    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle {
            val particle = PebbleImpactParticle(world, x, y, z, velocityX, velocityY, velocityZ)
            particle.setSprite(spriteProvider)
            return particle
        }
    }
}
