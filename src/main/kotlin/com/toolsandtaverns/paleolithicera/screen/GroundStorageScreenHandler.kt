package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.entity.GroundStorageBlockEntity
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos

/**
 * Handles server-side logic for the Ground Storage container (8 slots).
 */
class GroundStorageScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    private val inventory: Inventory,
    private val context: ScreenHandlerContext
) : ScreenHandler(ModScreenHandlers.GROUND_STORAGE, syncId) {

    companion object {
        const val STORAGE_COLUMNS = 4
        const val STORAGE_ROWS = 2
        const val STORAGE_START_X = 44
        const val STORAGE_START_Y = 17
        const val PLAYER_INVENTORY_START_X = 8
        const val PLAYER_INVENTORY_START_Y = 84
        const val HOTBAR_Y = 142
        const val SLOT_SPACING = 18
    }

    constructor(syncId: Int, playerInventory: PlayerInventory, pos: BlockPos) : this(
        syncId,
        playerInventory,
        (playerInventory.player.world.getBlockEntity(pos) as? GroundStorageBlockEntity)?.inventory
            ?: SimpleInventory(GroundStorageBlockEntity.SLOT_COUNT),
        ScreenHandlerContext.create(playerInventory.player.world, pos)
    )

    constructor(syncId: Int, playerInventory: PlayerInventory, entity: GroundStorageBlockEntity) : this(
        syncId,
        playerInventory,
        entity.inventory,
        ScreenHandlerContext.create(playerInventory.player.world, entity.pos)
    )

    init {
        checkSize(inventory, GroundStorageBlockEntity.SLOT_COUNT)
        inventory.onOpen(playerInventory.player)

        // Layout: 2 rows x 4 columns
        var index = 0
        for (row in 0 until STORAGE_ROWS) {
            for (col in 0 until STORAGE_COLUMNS) {
                val slotX = STORAGE_START_X + col * SLOT_SPACING
                val slotY = STORAGE_START_Y + row * SLOT_SPACING
                this.addSlot(object : Slot(inventory, index, slotX, slotY) {
                    override fun canInsert(stack: ItemStack): Boolean = stack.isStackable
                })
                index++
            }
        }

        // Player inventory slots (3 rows)
        for (row in 0..2) {
            for (col in 0..8) {
                val slotX = PLAYER_INVENTORY_START_X + col * SLOT_SPACING
                val slotY = PLAYER_INVENTORY_START_Y + row * SLOT_SPACING
                this.addSlot(Slot(playerInventory, col + row * 9 + 9, slotX, slotY))
            }
        }

        // Hotbar slots
        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, PLAYER_INVENTORY_START_X + col * SLOT_SPACING, HOTBAR_Y))
        }
    }

    override fun quickMove(player: PlayerEntity, slotIndex: Int): ItemStack {
        val slot = slots.getOrNull(slotIndex) ?: return ItemStack.EMPTY
        if (!slot.hasStack()) return ItemStack.EMPTY

        val original = slot.stack
        val copy = original.copy()

        return when (slotIndex) {
            // Ground storage slots (0..7) -> move to player inventory
            in 0..7 -> {
                if (!insertItem(original, 8, 44, true)) return ItemStack.EMPTY
                slot.markDirty()
                copy
            }
            // Player inventory/hotbar -> try move into ground storage
            in 8..43 -> {
                if (original.isStackable) {
                    if (!insertItem(original, 0, 8, false)) return ItemStack.EMPTY
                } else return ItemStack.EMPTY
                slot.markDirty()
                copy
            }
            else -> ItemStack.EMPTY
        }.also {
            if (original.isEmpty) slot.stack = ItemStack.EMPTY else slot.markDirty()
            if (original.count == copy.count) return ItemStack.EMPTY
            slot.onTakeItem(player, original)
        }
    }

    override fun canUse(player: PlayerEntity): Boolean = canUse(context, player, ModBlocks.GROUND_STORAGE)

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        inventory.onClose(player)
    }
}
