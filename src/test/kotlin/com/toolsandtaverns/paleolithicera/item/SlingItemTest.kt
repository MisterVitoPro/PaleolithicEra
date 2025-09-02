package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.registry.ModItems
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

@DisplayName("Sling Item Tests")
class SlingItemTest {
    
    @Test
    @DisplayName("Sling item should exist")
    fun testSlingItemExists() {
        val sling = ModItems.SLING
        assertNotNull(sling)
    }
    
    @Test
    @DisplayName("Sling should have correct max stack size")
    fun testSlingStackSize() {
        val sling = ModItems.SLING
        assertEquals(1, sling.maxCount)
    }
    
    @Test
    @DisplayName("Pebbles ammunition should exist")
    fun testPebblesItemExists() {
        val pebbles = ModItems.PEBBLES
        assertNotNull(pebbles)
    }
    
    @Test
    @DisplayName("Pebbles should stack correctly")
    fun testPebblesStackSize() {
        val pebbles = ModItems.PEBBLES
        assertEquals(64, pebbles.maxCount)
    }
}