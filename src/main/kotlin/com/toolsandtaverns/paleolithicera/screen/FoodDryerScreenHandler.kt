package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.entity.FoodDryerBlockEntity
import com.toolsandtaverns.paleolithicera.recipe.FoodDryingRecipe
import com.toolsandtaverns.paleolithicera.recipe.FoodDryingRecipeInput
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos

/**
 * Handles server-side inventory logic for the Food Dryer UI.
 * 
 * This screen handler manages the 4-slot food dryer interface, providing
 * specialized slots that only accept food items with valid drying recipes.
 * Each slot operates independently with its own progress tracking.
 */
class FoodDryerScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    pos: BlockPos,
) : ScreenHandler(ModScreenHandlers.FOOD_DRYER, syncId) {

    private val entity = playerInventory.player.world.getBlockEntity(pos) as FoodDryerBlockEntity
    private val inventory: SimpleInventory = entity.inventory
    private val context = ScreenHandlerContext.create(playerInventory.player.world, pos)

    init {
        checkSize(inventory, FoodDryerBlockEntity.SLOT_COUNT)
        inventory.onOpen(playerInventory.player)

        // Add the 4 food dryer slots in a 2x2 grid layout
        // Top row
        this.addSlot(FoodDryerSlot(inventory, 0, 44, 17))  // Top-left
        this.addSlot(FoodDryerSlot(inventory, 1, 116, 17)) // Top-right
        
        // Bottom row  
        this.addSlot(FoodDryerSlot(inventory, 2, 44, 50))  // Bottom-left
        this.addSlot(FoodDryerSlot(inventory, 3, 116, 50)) // Bottom-right

        // Player inventory slots (main inventory)
        for (row in 0..2) {
            for (col in 0..8) {
                this.addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }

        // Player hotbar slots
        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
        
        // Add properties for progress tracking of all 4 slots
        addProperties(entity.propertyDelegate)
    }

    /**
     * Custom slot for the food dryer that only accepts items with valid drying recipes.
     */
    private inner class FoodDryerSlot(
        inventory: SimpleInventory, 
        index: Int, 
        x: Int, 
        y: Int
    ) : Slot(inventory, index, x, y) {
        
        override fun canInsert(stack: ItemStack): Boolean {
            return isValidForDrying(stack)
        }

        override fun getMaxItemCount(): Int {
            return 1 // Each slot can only hold 1 item
        }
    }

    override fun quickMove(
        player: PlayerEntity,
        slotIndex: Int
    ): ItemStack {
        val slot = slots.getOrNull(slotIndex) ?: return ItemStack.EMPTY
        if (!slot.hasStack()) return ItemStack.EMPTY

        val originalStack = slot.stack
        val newStack = originalStack.copy()

        return when (slotIndex) {
            // Food dryer slots (0-3)
            in 0..3 -> {
                // Move from dryer to player inventory
                if (!insertItem(originalStack, 4, 40, true)) {
                    return ItemStack.EMPTY
                }
                slot.markDirty()
                newStack
            }
            
            // Player inventory slots (4-39: main inventory + hotbar)
            in 4..39 -> {
                // Try to move valid items to dryer slots
                if (isValidForDrying(originalStack)) {
                    if (!insertItem(originalStack, 0, 4, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    return ItemStack.EMPTY
                }
                slot.markDirty()
                newStack
            }
            
            else -> ItemStack.EMPTY
        }.also {
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
            if (originalStack.count == newStack.count) {
                return ItemStack.EMPTY
            }
            slot.onTakeItem(player, originalStack)
        }
    }

    /**
     * Checks if an item stack is valid for food drying.
     * Accepts only items that have a matching Food Drying recipe (i.e., have a dried variant).
     * Dried foods (outputs) and unrelated items are rejected.
     */
    private fun isValidForDrying(stack: ItemStack): Boolean {
        val world = entity.world
        // On client, allow tentative insert; server validates authoritatively
        val serverWorld = world as? net.minecraft.server.world.ServerWorld ?: return true

        val input = FoodDryingRecipeInput(stack)
        val match = serverWorld.recipeManager.getFirstMatch(
            ModRecipes.FOOD_DRYING_RECIPE_TYPE,
            input,
            serverWorld
        )
        return match.isPresent
    }

    override fun canUse(player: PlayerEntity): Boolean = canUse(context, player, ModBlocks.FOOD_DRYER)

    /**
     * Returns scaled progress for a specific slot for GUI rendering.
     */
    fun getScaledProgress(slot: Int, maxWidth: Int): Int {
        if (slot < 0 || slot >= FoodDryerBlockEntity.SLOT_COUNT) return 0
        val progress = entity.propertyDelegate.get(slot)
        return if (progress > 0) {
            (progress * maxWidth) / FoodDryerBlockEntity.DRYING_DURATION_TICKS
        } else 0
    }

    /**
     * Gets the current progress percentage for a specific slot.
     */
    fun getSlotProgressPercent(slot: Int): Int {
        return entity.getSlotProgress(slot)
    }

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        inventory.onClose(player)
    }
}
