package com.toolsandtaverns.paleolithicera.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

/** Client GUI for the eight-slot ground storage container. */
class GroundStorageScreen(
    handler: GroundStorageScreenHandler,
    val playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<GroundStorageScreenHandler>(handler, playerInventory, title) {

    companion object {
        private val PANEL_BORDER = 0xFF1F1F1F.toInt()
        private val PANEL_COLOR = 0xFFC6C6C6.toInt()
        private val RECESS_SHADOW = 0xFF555555.toInt()
        private val RECESS_HIGHLIGHT = 0xFFFFFFFF.toInt()
        private val SLOT_COLOR = 0xFF8B8B8B.toInt()
    }

    init {
        backgroundWidth = 176
        backgroundHeight = 166
        playerInventoryTitleY = backgroundHeight - 94
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        drawPanel(context)

        // Four-by-two ground-storage grid.
        drawRecessedArea(context, x + 39, y + 12, 80, 44)
        for (row in 0 until GroundStorageScreenHandler.STORAGE_ROWS) {
            for (col in 0 until GroundStorageScreenHandler.STORAGE_COLUMNS) {
                drawSlotFrame(
                    context,
                    x + GroundStorageScreenHandler.STORAGE_START_X + col * GroundStorageScreenHandler.SLOT_SPACING,
                    y + GroundStorageScreenHandler.STORAGE_START_Y + row * GroundStorageScreenHandler.SLOT_SPACING
                )
            }
        }

        // Player inventory and hotbar use the exact coordinates declared by the handler.
        drawRecessedArea(context, x + 4, y + 80, 168, 60)
        for (row in 0..2) {
            for (col in 0..8) {
                drawSlotFrame(
                    context,
                    x + GroundStorageScreenHandler.PLAYER_INVENTORY_START_X +
                        col * GroundStorageScreenHandler.SLOT_SPACING,
                    y + GroundStorageScreenHandler.PLAYER_INVENTORY_START_Y +
                        row * GroundStorageScreenHandler.SLOT_SPACING
                )
            }
        }

        drawRecessedArea(context, x + 4, y + 138, 168, 26)
        for (col in 0..8) {
            drawSlotFrame(
                context,
                x + GroundStorageScreenHandler.PLAYER_INVENTORY_START_X +
                    col * GroundStorageScreenHandler.SLOT_SPACING,
                y + GroundStorageScreenHandler.HOTBAR_Y
            )
        }
    }

    private fun drawPanel(context: DrawContext) {
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, PANEL_BORDER)
        context.fill(x + 1, y + 1, x + backgroundWidth - 1, y + backgroundHeight - 1, PANEL_COLOR)
    }

    private fun drawRecessedArea(context: DrawContext, left: Int, top: Int, width: Int, height: Int) {
        context.fill(left, top, left + width, top + height, RECESS_HIGHLIGHT)
        context.fill(left, top, left + width - 1, top + height - 1, RECESS_SHADOW)
        context.fill(left + 1, top + 1, left + width - 1, top + height - 1, PANEL_COLOR)
    }

    private fun drawSlotFrame(context: DrawContext, slotX: Int, slotY: Int) {
        context.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, RECESS_HIGHLIGHT)
        context.fill(slotX - 1, slotY - 1, slotX + 16, slotY + 16, RECESS_SHADOW)
        context.fill(slotX, slotY, slotX + 16, slotY + 16, SLOT_COLOR)
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false)
        context.drawText(textRenderer, playerInventory.displayName, 8, playerInventoryTitleY, 0x404040, false)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun shouldPause(): Boolean = false
}
