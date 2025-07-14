package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipeInput
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot


/**
 * Screen handler for the Knapping Station. Manages the input/output inventory and updates crafting output.
 */
class KnappingScreenHandler(
    syncId: Int,
    private val playerInventory: PlayerInventory,
    private val inventory: Inventory = SimpleInventory(5) // 4 inputs + 1 output
) : ScreenHandler(ModScreenHandlers.KNAPPING, syncId) {

    init {
        checkSize(inventory, 5)
        inventory.onOpen(playerInventory.player)

        // Input slots (0–3)
        for (i in 0 until 4) {
            val x = 30 + (i % 2) * 18
            val y = 17 + (i / 2) * 18
            addSlot(object : Slot(inventory, i, x, y) {
                override fun markDirty() {
                    super.markDirty()
                    updateResult()
                }
            })
        }

        // Output slot (4)
        addSlot(object : Slot(inventory, 4, 124, 35) {
            override fun canInsert(stack: ItemStack): Boolean = false

            override fun onTakeItem(player: PlayerEntity, takenStack: ItemStack) {
                // Consume one item from each input slot
                for (i in 0 until 4) {
                    val inputStack = inventory.getStack(i)
                    if (!inputStack.isEmpty) {
                        inputStack.decrement(1)
                    }
                }

                // Recalculate result
                updateResult()
                super.onTakeItem(player, takenStack)
            }
        })

        // Player inventory (3 rows × 9)
        for (row in 0..2) {
            for (col in 0..8) {
                addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }

        // Hotbar
        for (col in 0..8) {
            addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
    }

    override fun canUse(player: PlayerEntity): Boolean = true

    private fun updateResult() {
        val world = playerInventory.player.world
        val server = world.server ?: return
        val recipeManager = server.recipeManager

        val inputStacks = (0 until 4).map { inventory.getStack(it).copy() }
        val recipeInput = KnapRecipeInput(inputStacks)

        // Get the first matching recipe entry
        val recipeEntry = recipeManager
            .getAllMatches(ModRecipes.KNAPPING_RECIPE_TYPE, recipeInput, world)
            .findFirst()

        // Unwrap entry and call .craft on the recipe itself
        val result: ItemStack = recipeEntry
            .map { it.value.craft(recipeInput, world.registryManager) }
            .orElse(ItemStack.EMPTY)!!
            .copy()

        LOGGER.info("Crafted result: ${result.count}x ${result.item}")

        inventory.setStack(4, result)
        sendContentUpdates()
    }

    override fun quickMove(player: PlayerEntity, index: Int): ItemStack {
        val slot = slots.getOrNull(index) ?: return ItemStack.EMPTY
        if (!slot.hasStack()) return ItemStack.EMPTY

        val originalStack = slot.stack
        val newStack = originalStack.copy()

        return when (index) {
            // Output slot
            4 -> {
                if (!insertItem(originalStack, 5, 41, true)) return ItemStack.EMPTY
                slot.onQuickTransfer(originalStack, newStack)
                slot.markDirty()
                newStack
            }

            // Player inventory (main + hotbar)
            in 5..40 -> {
                if (!insertItem(originalStack, 0, 4, false)) return ItemStack.EMPTY // Try putting into input slots
                slot.markDirty()
                newStack
            }

            // Input slots (0–3)
            in 0..3 -> {
                if (!insertItem(originalStack, 5, 41, false)) return ItemStack.EMPTY // Back to player inventory
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

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        inventory.onClose(player)
    }

}
