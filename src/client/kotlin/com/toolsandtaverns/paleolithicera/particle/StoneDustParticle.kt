package com.toolsandtaverns.paleolithicera.particle

import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.MathHelper

/**
 * Fine dust particle effect for stone impacts.
 * 
 * This particle represents fine stone dust that is kicked up when pebbles
 * hit hard surfaces. These particles are smaller, lighter, and last longer
 * than the main impact particles, creating a more realistic debris cloud.
 */
class StoneDustParticle(
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
        // Color range for fine dust - lighter than debris particles
        private const val MIN_RED = 0.6f
        private const val MAX_RED = 0.8f
        private const val MIN_GREEN = 0.5f
        private const val MAX_GREEN = 0.7f
        private const val MIN_BLUE = 0.4f
        private const val MAX_BLUE = 0.6f
        
        // Particle physics constants for dust
        private const val MIN_LIFETIME = 20
        private const val MAX_LIFETIME = 40
        private const val GRAVITY_MODIFIER = 0.1f // Very light particles
        private const val AIR_RESISTANCE = 0.95f
        private const val MIN_SCALE = 0.05f
        private const val MAX_SCALE = 0.15f
    }

    init {
        // Dust particles last longer than debris
        this.maxAge = MIN_LIFETIME + world.random.nextInt(MAX_LIFETIME - MIN_LIFETIME)
        
        // Dust particles are smaller
        this.scale = MIN_SCALE + world.random.nextFloat() * (MAX_SCALE - MIN_SCALE)
        
        // Lighter, dustier colors
        this.red = MIN_RED + world.random.nextFloat() * (MAX_RED - MIN_RED)
        this.green = MIN_GREEN + world.random.nextFloat() * (MAX_GREEN - MIN_GREEN)
        this.blue = MIN_BLUE + world.random.nextFloat() * (MAX_BLUE - MIN_BLUE)
        
        // Start more transparent since it's fine dust
        this.alpha = 0.4f + world.random.nextFloat() * 0.3f
        initialScale = scale
        initialAlpha = alpha
        
        // Much less gravity - dust floats
        this.gravityStrength = GRAVITY_MODIFIER
        
        // Reduce initial velocity more than debris particles
        this.velocityX = velocityX * 0.1
        this.velocityY = velocityY * 0.1 + 0.01 // Slight upward bias
        this.velocityZ = velocityZ * 0.1
    }

    override fun getType(): ParticleTextureSheet {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun tick() {
        super.tick()
        
        // Apply stronger air resistance for dust
        this.velocityX *= AIR_RESISTANCE
        this.velocityY *= AIR_RESISTANCE
        this.velocityZ *= AIR_RESISTANCE
        
        // Fade out more gradually than debris
        val lifeProgress = this.age.toFloat() / this.maxAge.toFloat()
        this.alpha = MathHelper.lerp(lifeProgress, initialAlpha, 0.0f)
        
        // Dust particles don't shrink as much
        this.scale = MathHelper.lerp(lifeProgress, initialScale, initialScale * 0.8f)
    }

    /**
     * Factory for creating StoneDustParticle instances.
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
            val particle = StoneDustParticle(world, x, y, z, velocityX, velocityY, velocityZ)
            particle.setSprite(spriteProvider)
            return particle
        }
    }
}
