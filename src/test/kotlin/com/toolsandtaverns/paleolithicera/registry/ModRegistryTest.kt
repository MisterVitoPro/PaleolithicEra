package com.toolsandtaverns.paleolithicera.registry

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

@DisplayName("Mod Registry Tests")
class ModRegistryTest {
    
    @Test
    @DisplayName("Should have mod items registry initialized")
    fun testModItemsRegistryExists() {
        // Test that the ModItems object can be referenced
        assertNotNull(ModItems)
    }
    
    @Test
    @DisplayName("Should have mod blocks registry initialized")
    fun testModBlocksRegistryExists() {
        // Test that the ModBlocks object can be referenced
        assertNotNull(ModBlocks)
    }
    
    @Test
    @DisplayName("Should have mod entity types registry initialized")
    fun testModEntityTypesRegistryExists() {
        // Test that the ModEntityType object can be referenced
        assertNotNull(ModEntityType)
    }
    
    @Test
    @DisplayName("Should have mod particle types registry initialized") 
    fun testModParticleTypesRegistryExists() {
        // Test that the ModParticleTypes object can be referenced
        assertNotNull(ModParticleTypes)
    }
    
    @Test
    @DisplayName("Key items should be registered")
    fun testKeyItemsRegistered() {
        // Test that important items are registered
        assertNotNull(ModItems.ROCK_CHUNK)
        assertNotNull(ModItems.PLANT_FIBER)
        assertNotNull(ModItems.SLING)
        assertNotNull(ModItems.PEBBLES)
        assertNotNull(ModItems.WOODEN_SPEAR)
        assertNotNull(ModItems.BONE_SPEAR)
    }
    
    @Test
    @DisplayName("Key blocks should be registered")
    fun testKeyBlocksRegistered() {
        // Test that important blocks are registered
        assertNotNull(ModBlocks.KNAPPING_STATION)
        assertNotNull(ModBlocks.CRUDE_CAMPFIRE)
        assertNotNull(ModBlocks.HIDE_DRYER)
        assertNotNull(ModBlocks.CRUDE_BED)
    }
}