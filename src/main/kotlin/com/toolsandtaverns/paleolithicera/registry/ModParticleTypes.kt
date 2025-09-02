package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

/**
 * Registry for custom particle types used throughout the Paleolithic Era mod.
 * 
 * This object manages the registration of all custom particle effects,
 * including impact particles for projectiles and environmental effects.
 */
object ModParticleTypes {
    
    /**
     * Particle effect spawned when a pebble projectile impacts with blocks or entities.
     * Creates small dust and debris particles appropriate for stone-age weaponry.
     */
    val PEBBLE_IMPACT: SimpleParticleType = Registry.register(
        Registries.PARTICLE_TYPE,
        id("pebble_impact"),
        FabricParticleTypes.simple()
    )
    
    /**
     * Secondary particle effect for stone dust when pebbles hit hard surfaces.
     * Creates smaller, lighter particles that float longer in the air.
     */
    val STONE_DUST: SimpleParticleType = Registry.register(
        Registries.PARTICLE_TYPE,
        id("stone_dust"),
        FabricParticleTypes.simple()
    )
    
    /**
     * Initializes the particle type registry.
     * Called during mod initialization to register all custom particle types.
     */
    fun initialize() {
        // Particle types are registered during object initialization
        // This method serves as an initialization hook for the mod
    }
}