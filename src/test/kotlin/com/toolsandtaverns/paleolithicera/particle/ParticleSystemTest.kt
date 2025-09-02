package com.toolsandtaverns.paleolithicera.particle

import com.toolsandtaverns.paleolithicera.registry.ModParticleTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

@DisplayName("Particle System Tests")
class ParticleSystemTest {
    
    @Test
    @DisplayName("Pebble impact particle type should be registered")
    fun testPebbleImpactParticleRegistered() {
        assertNotNull(ModParticleTypes.PEBBLE_IMPACT)
    }
    
    @Test
    @DisplayName("Stone dust particle type should be registered")
    fun testStoneDustParticleRegistered() {
        assertNotNull(ModParticleTypes.STONE_DUST)
    }
    
    @Test
    @DisplayName("Particle types should have unique identifiers")
    fun testParticleTypesUnique() {
        val pebbleImpact = ModParticleTypes.PEBBLE_IMPACT
        val stoneDust = ModParticleTypes.STONE_DUST
        
        assertNotEquals(pebbleImpact, stoneDust)
    }
}