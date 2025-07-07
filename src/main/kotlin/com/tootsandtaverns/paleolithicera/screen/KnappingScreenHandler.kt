package com.tootsandtaverns.paleolithicera.screen

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import com.tootsandtaverns.paleolithicera.PaleolithicEra.logger
import com.tootsandtaverns.paleolithicera.recipe.KnapRecipe
import com.tootsandtaverns.paleolithicera.registry.ModRecipes
import com.tootsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
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
                override fun setStack(stack: ItemStack) {
                    super.setStack(stack)
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

    /**
     * Tries to find a matching shapeless vanilla recipe with prefix "knap_" and sets the output.
     */
    private fun updateResult() {
        val world = playerInventory.player.world
        val server = world.server ?: return
        val recipeManager = server.recipeManager

        // Fill a CraftingInventory from the 4 input slots
        val craftingInventory = CraftingInventory(this, 2, 2).apply {
            for (i in 0 until 4) {
                setStack(i, inventory.getStack(i))
            }
        }

        // Convert CraftingInventory into a valid CraftingRecipeInput
        val recipeInput = craftingInventory.createRecipeInput()

        recipeManager.getAllMatches(ModRecipes.KNAPPING_RECIPE_TYPE, recipeInput, world).forEach {
            logger.info("Found recipe: ${it}")
        }

        // Now get all matching recipes using the proper input
        val recipe: KnapRecipe? = recipeManager
            .getAllMatches(ModRecipes.KNAPPING_RECIPE_TYPE, recipeInput, world)
            .toList()
            .firstOrNull { it.id.value.namespace == MOD_ID && it.id.value.path.startsWith("knap_") }
            ?.value

        val result = recipe
            ?.craft(recipeInput, world.registryManager)
            ?.copy()

        logger.info("Crafted result: ${result?.count}x ${result?.item}")

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
