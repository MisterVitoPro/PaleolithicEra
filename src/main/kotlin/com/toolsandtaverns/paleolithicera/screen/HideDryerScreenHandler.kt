package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.entity.HideDryerBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos

/**
 * Handles server-side inventory logic for the Hide Dryer UI.
 */
class HideDryerScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    pos: BlockPos,
) : ScreenHandler(ModScreenHandlers.HIDE_DRYER, syncId) {

    private val entity = playerInventory.player.world.getBlockEntity(pos) as HideDryerBlockEntity
    private val inventory: SimpleInventory = entity.inventory

    init {
        checkSize(inventory, 2)
        inventory.onOpen(playerInventory.player)

        // Input slot (left)
        this.addSlot(object : Slot(inventory, 0, 44, 35) {
            override fun canInsert(stack: ItemStack): Boolean {
                return stack.isOf(ModItems.RAWHIDE)
            }
        })

        // Output slot (right)
        this.addSlot(object : Slot(inventory, 1, 116, 35) {
            override fun canInsert(stack: ItemStack) = false
        })

        // Player inventory slots
        for (row in 0..2) {
            for (col in 0..8) {
                this.addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }

        // Hotbar slots
        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
        addProperties(entity.propertyDelegate)
    }

    override fun quickMove(
        player: PlayerEntity?,
        slotIndx: Int
    ): ItemStack? {
        val slot = slots.getOrNull(slotIndx) ?: return ItemStack.EMPTY
        if (!slot.hasStack()) return ItemStack.EMPTY

        val originalStack = slot.stack
        val newStack = originalStack.copy()

        return when (slotIndx) {
            // Input slot (0)
            0 -> {
                if (!insertItem(originalStack, 2, 38, false)) return ItemStack.EMPTY // Back to player inventory
                slot.markDirty()
                newStack
            }
            // Output slot (1)
            1 -> {
                if (!insertItem(originalStack, 2, 38, true)) return ItemStack.EMPTY
                slot.onQuickTransfer(originalStack, newStack)
                slot.markDirty()
                newStack
            }

            // Player inventory (main + hotbar)
            in 2..38 -> {
                if (!insertItem(originalStack, 0, 1, false)) return ItemStack.EMPTY // Try putting into input slots
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

    override fun canUse(player: PlayerEntity): Boolean = inventory.canPlayerUse(player)

    /**
     * Returns progress as a percent from 0 to 100.
     */
    fun getScaledProgress(maxWidth: Int): Int {
        val progress = entity.propertyDelegate.get(0)
        return (progress * maxWidth) / HideDryerBlockEntity.DRYING_DURATION_TICKS
    }
}
