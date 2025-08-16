package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos


/**
 * Screen handler for the Knapping Station. Manages the input/output inventory and updates crafting output.
 */
class KnappingScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val blockEntity: KnappingStationBlockEntity
) : ScreenHandler(ModScreenHandlers.KNAPPING, syncId) {

    private val inventory = blockEntity.getInventory()

    constructor(syncId: Int, inventory: PlayerInventory, pos: BlockPos) : this(
        syncId,
        inventory,
        inventory.player.world.getBlockEntity(pos) as KnappingStationBlockEntity,
    )

    init {
        checkSize(inventory, 2)
        inventory.onOpen(playerInventory.player)

        // Input slot (0)
        addSlot(object : Slot(inventory, 0, 54, 34) {
            override fun canInsert(stack: ItemStack): Boolean = true
        })

        // Output slot (1)
        addSlot(object : Slot(inventory, 1, 104, 34) {
            override fun canInsert(stack: ItemStack): Boolean = false
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

    override fun quickMove(player: PlayerEntity, index: Int): ItemStack {
        val slot = slots.getOrNull(index) ?: return ItemStack.EMPTY
        if (!slot.hasStack()) return ItemStack.EMPTY

        val originalStack = slot.stack
        val newStack = originalStack.copy()

        return when (index) {
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

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        inventory.onClose(player)
    }

}
