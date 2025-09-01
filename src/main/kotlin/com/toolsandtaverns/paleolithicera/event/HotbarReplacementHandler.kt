package com.toolsandtaverns.paleolithicera.event

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles automatic hotbar replacement when items are consumed or broken.
 * 
 * This system automatically finds identical items in the player's inventory
 * and moves them to the hotbar slot that was just emptied, maintaining
 * the player's workflow without interruption.
 */
object HotbarReplacementHandler {

    // Track player inventories to detect changes
    private val playerInventoryTracking = ConcurrentHashMap<UUID, Array<ItemStack>>()
    
    // Track recent replacement actions to prevent infinite loops
    private val recentReplacements = ConcurrentHashMap<UUID, MutableSet<ReplacementAction>>()
    
    // Cooldown period in ticks to prevent rapid successive replacements
    private const val REPLACEMENT_COOLDOWN_TICKS = 5L
    
    // Server tick counter for timestamp tracking
    private var serverTicks = 0L
    
    /**
     * Data class to track replacement actions and prevent loops.
     */
    private data class ReplacementAction(
        val fromSlot: Int,
        val toSlot: Int,
        val timestamp: Long
    )

    /**
     * Registers all event handlers for hotbar replacement functionality.
     */
    fun register() {
        // Monitor inventory changes every server tick
        ServerTickEvents.END_SERVER_TICK.register { server ->
            serverTicks++
            cleanupOldReplacements()
            
            server.playerManager.playerList.forEach { player ->
                if (player is ServerPlayerEntity) {
                    checkPlayerInventory(player)
                }
            }
        }

        // Handle tool breaking during block mining
        PlayerBlockBreakEvents.BEFORE.register(PlayerBlockBreakEvents.Before { world, player, pos, state, blockEntity ->
            if (!world.isClient && player is ServerPlayerEntity) {
                val heldStack = player.mainHandStack
                
                // Check if the tool will break after this use
                if (!heldStack.isEmpty && heldStack.isDamageable && heldStack.damage + 1 >= heldStack.maxDamage) {
                    val originalStack = heldStack.copy()
                    val slotIndex = player.inventory.selectedSlot
                    
                    // Schedule replacement for after the tool breaks
                    world.server?.execute {
                        checkAndReplaceItem(player, slotIndex, originalStack)
                    }
                }
            }
            true // Allow the block break to continue
        })

        // Clean up tracking when players disconnect
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val playerId = handler.player.uuid
            playerInventoryTracking.remove(playerId)
            recentReplacements.remove(playerId)
        }
    }

    /**
     * Removes old replacement actions that are outside the cooldown period.
     */
    private fun cleanupOldReplacements() {
        val cutoffTime = serverTicks - REPLACEMENT_COOLDOWN_TICKS
        recentReplacements.values.forEach { playerReplacements ->
            playerReplacements.removeIf { it.timestamp < cutoffTime }
        }
    }

    /**
     * Checks if a replacement from one slot to another would create a loop.
     */
    private fun wouldCreateLoop(playerId: UUID, fromSlot: Int, toSlot: Int): Boolean {
        val playerReplacements = recentReplacements[playerId] ?: return false
        val cutoffTime = serverTicks - REPLACEMENT_COOLDOWN_TICKS
        
        return playerReplacements.any { replacement ->
            replacement.timestamp >= cutoffTime && 
            ((replacement.fromSlot == toSlot && replacement.toSlot == fromSlot) ||
             (replacement.fromSlot == fromSlot || replacement.toSlot == toSlot))
        }
    }

    /**
     * Records a replacement action to track it for loop prevention.
     */
    private fun recordReplacement(playerId: UUID, fromSlot: Int, toSlot: Int) {
        val playerReplacements = recentReplacements.getOrPut(playerId) { mutableSetOf() }
        playerReplacements.add(ReplacementAction(fromSlot, toSlot, serverTicks))
    }

    /**
     * Checks a player's inventory for changes and performs replacement if needed.
     */
    private fun checkPlayerInventory(player: ServerPlayerEntity) {
        val playerId = player.uuid
        val currentHotbar = Array(9) { i -> player.inventory.getStack(i).copy() }
        val previousHotbar = playerInventoryTracking[playerId]
        
        if (previousHotbar != null) {
            // Check each hotbar slot for changes
            for (i in 0..8) {
                val previous = previousHotbar[i]
                val current = currentHotbar[i]
                
                // Only trigger replacement for significant changes (item consumed/broken)
                // Ignore minor count changes that might be from stacking operations
                val wasItemCompletelyRemoved = !previous.isEmpty && current.isEmpty
                val wasItemSignificantlyReduced = !previous.isEmpty && !current.isEmpty && 
                    current.count < previous.count && current.count < (previous.count * 0.5)
                
                if (wasItemCompletelyRemoved || wasItemSignificantlyReduced) {
                    // Only proceed if this wouldn't create a replacement loop
                    if (!hasRecentReplacementActivity(playerId, i)) {
                        // Schedule replacement check for next tick to avoid conflicts
                        player.server?.execute {
                            checkAndReplaceItem(player, i, previous)
                        }
                    }
                }
            }
        }
        
        // Update tracking
        playerInventoryTracking[playerId] = currentHotbar
    }

    /**
     * Checks if there has been recent replacement activity involving this slot.
     */
    private fun hasRecentReplacementActivity(playerId: UUID, slotIndex: Int): Boolean {
        val playerReplacements = recentReplacements[playerId] ?: return false
        val cutoffTime = serverTicks - REPLACEMENT_COOLDOWN_TICKS
        
        return playerReplacements.any { replacement ->
            replacement.timestamp >= cutoffTime && 
            (replacement.fromSlot == slotIndex || replacement.toSlot == slotIndex)
        }
    }

    /**
     * Checks if an item needs replacement and performs the replacement if possible.
     * 
     * @param player The player whose inventory to search
     * @param slotIndex The hotbar slot index that needs replacement
     * @param originalStack The original item stack that was consumed/broken
     */
    private fun checkAndReplaceItem(player: ServerPlayerEntity, slotIndex: Int, originalStack: ItemStack) {
        val playerId = player.uuid
        
        val currentStack = when (slotIndex) {
            40 -> player.offHandStack // Off-hand slot
            in 0..8 -> player.inventory.getStack(slotIndex) // Hotbar slots
            else -> return // Invalid slot
        }
        
        // Only replace if the slot is now empty or has fewer items than before
        if (!currentStack.isEmpty && currentStack.count >= originalStack.count) {
            return
        }
        
        // Find a replacement item in the inventory
        val replacementSlot = findReplacementItem(player, originalStack, slotIndex)
        if (replacementSlot != -1) {
            // Check if this replacement would create a loop
            if (!wouldCreateLoop(playerId, replacementSlot, slotIndex)) {
                recordReplacement(playerId, replacementSlot, slotIndex)
                moveItemToSlot(player, replacementSlot, slotIndex)
            }
        }
    }

    /**
     * Finds an identical item in the player's inventory that can be used as a replacement.
     * 
     * @param player The player whose inventory to search
     * @param targetStack The item stack to find a replacement for
     * @param excludeSlot The slot to exclude from search (typically the slot being replaced)
     * @return The slot index of the replacement item, or -1 if none found
     */
    private fun findReplacementItem(player: ServerPlayerEntity, targetStack: ItemStack, excludeSlot: Int): Int {
        val inventory = player.inventory
        
        // Search through the main inventory (excluding hotbar) - this is the primary source
        for (i in 9 until inventory.size()) {
            val stack = inventory.getStack(i)
            if (!stack.isEmpty && areItemsIdentical(stack, targetStack)) {
                return i
            }
        }
        
        // Only search through hotbar as a last resort and exclude the target slot
        // This prevents moving items back and forth between hotbar slots
        for (i in 0..8) {
            if (i != excludeSlot) {
                val stack = inventory.getStack(i)
                if (!stack.isEmpty && areItemsIdentical(stack, targetStack)) {
                    return i
                }
            }
        }
        
        return -1 // No replacement found
    }

    /**
     * Checks if two item stacks are identical for replacement purposes.
     * This ensures we only replace with the exact same item, not similar items.
     * 
     * @param stack1 First item stack to compare
     * @param stack2 Second item stack to compare
     * @return true if the items are identical for replacement purposes
     */
    private fun areItemsIdentical(stack1: ItemStack, stack2: ItemStack): Boolean {
        if (stack1.item != stack2.item) {
            return false
        }
        
        // For damageable items (like tools and weapons), we consider them identical
        // if they're the same item type regardless of damage
        if (stack1.isDamageable && stack2.isDamageable) {
            // For damageable items, just check if they're the same item type
            // We don't need to compare damage values
            return true
        }
        
        // For non-damageable items, all components must match exactly
        return stack1.components == stack2.components
    }

    /**
     * Moves an item from one slot to another, handling stacking properly.
     * 
     * @param player The player whose inventory to modify
     * @param fromSlot The source slot index
     * @param toSlot The destination slot index
     */
    private fun moveItemToSlot(player: ServerPlayerEntity, fromSlot: Int, toSlot: Int) {
        val inventory = player.inventory
        val fromStack = inventory.getStack(fromSlot)
        
        if (fromStack.isEmpty) return
        
        when (toSlot) {
            40 -> { // Off-hand slot
                val currentOffHand = player.offHandStack
                if (currentOffHand.isEmpty) {
                    // Move entire stack to off-hand
                    player.setStackInHand(Hand.OFF_HAND, fromStack.copy())
                    inventory.setStack(fromSlot, ItemStack.EMPTY)
                } else if (areItemsIdentical(currentOffHand, fromStack)) {
                    // Try to stack with existing off-hand item
                    val transferAmount = minOf(fromStack.count, currentOffHand.maxCount - currentOffHand.count)
                    if (transferAmount > 0) {
                        currentOffHand.increment(transferAmount)
                        fromStack.decrement(transferAmount)
                        if (fromStack.isEmpty) {
                            inventory.setStack(fromSlot, ItemStack.EMPTY)
                        }
                    }
                }
            }
            in 0..8 -> { // Hotbar slots
                val currentStack = inventory.getStack(toSlot)
                if (currentStack.isEmpty) {
                    // Move entire stack to hotbar
                    inventory.setStack(toSlot, fromStack.copy())
                    inventory.setStack(fromSlot, ItemStack.EMPTY)
                } else if (areItemsIdentical(currentStack, fromStack)) {
                    // Try to stack with existing hotbar item
                    val transferAmount = minOf(fromStack.count, currentStack.maxCount - currentStack.count)
                    if (transferAmount > 0) {
                        currentStack.increment(transferAmount)
                        fromStack.decrement(transferAmount)
                        if (fromStack.isEmpty) {
                            inventory.setStack(fromSlot, ItemStack.EMPTY)
                        }
                    }
                }
            }
        }
        
        // Sync inventory with client
        player.playerScreenHandler.sendContentUpdates()
    }
}