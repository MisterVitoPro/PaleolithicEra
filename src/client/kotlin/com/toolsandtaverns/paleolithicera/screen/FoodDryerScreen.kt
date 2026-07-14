package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Identifier

/**
 * Client-side GUI screen for the Food Dryer.
 * 
 * Displays a 2x2 grid of food drying slots, each with individual progress bars
 * showing the drying progress. The interface provides visual feedback for the
 * food preservation process, with progress indicators for each slot.
 */
class FoodDryerScreen(
    handler: FoodDryerScreenHandler,
    val playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<FoodDryerScreenHandler>(handler, playerInventory, title) {

    companion object {
        val TEXTURE: Identifier = id("textures/gui/container/food_dryer.png")
        
        // Progress bar dimensions and positions for each slot
        private const val PROGRESS_BAR_WIDTH = 16
        private const val PROGRESS_BAR_HEIGHT = 2
        
        // Progress bar positions for each slot (below each slot)
        private val PROGRESS_POSITIONS = arrayOf(
            Pair(44, 35),  // Slot 0 (top-left)
            Pair(116, 35), // Slot 1 (top-right)
            Pair(44, 68),  // Slot 2 (bottom-left)
            Pair(116, 68)  // Slot 3 (bottom-right)
        )
    }

    init {
        backgroundWidth = 176
        backgroundHeight = 166
        playerInventoryTitleY = backgroundHeight - 94
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        // Draw main GUI background
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x, y,
            0f, 0f,
            backgroundWidth, backgroundHeight,
            256, 256
        )

        // Draw progress bars for each slot
        for (slot in 0 until 4) {
            val progressWidth = handler.getScaledProgress(slot, PROGRESS_BAR_WIDTH)
            if (progressWidth > 0) {
                val (progressX, progressY) = PROGRESS_POSITIONS[slot]
                
                // Draw progress bar background
                context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + progressX,
                    y + progressY,
                    176f, 0f, // Background texture position
                    PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT,
                    256, 256
                )
                
                // Draw progress fill
                context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + progressX,
                    y + progressY,
                    176f, 16f, // Progress fill texture position
                    progressWidth, PROGRESS_BAR_HEIGHT,
                    256, 256
                )
            }
        }
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        // Draw title
        context.drawText(
            textRenderer, 
            title, 
            titleX, 
            titleY, 
            Colors.DARK_GRAY, 
            false
        )
        
        // Draw player inventory label
        context.drawText(
            textRenderer, 
            playerInventory.displayName, 
            8, 
            playerInventoryTitleY, 
            Colors.DARK_GRAY, 
            false
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
        
        // Draw progress tooltips when hovering over progress bars
        drawProgressTooltips(context, mouseX, mouseY)
    }

    /**
     * Draws tooltips showing drying progress when hovering over progress bars.
     */
    private fun drawProgressTooltips(context: DrawContext, mouseX: Int, mouseY: Int) {
        for (slot in 0 until 4) {
            val (progressX, progressY) = PROGRESS_POSITIONS[slot]
            val barX = x + progressX
            val barY = y + progressY
            
            // Check if mouse is hovering over this progress bar
            if (mouseX >= barX && mouseX < barX + PROGRESS_BAR_WIDTH && 
                mouseY >= barY && mouseY < barY + PROGRESS_BAR_HEIGHT) {
                
                val progressPercent = handler.getSlotProgressPercent(slot)
                if (progressPercent > 0) {
                    val tooltipText = Text.translatable(
                        "gui.paleolithic-era.food_dryer.progress",
                        progressPercent
                    )
                    context.drawTooltip(textRenderer, tooltipText, mouseX, mouseY)
                }
                break
            }
        }
    }

    override fun shouldPause(): Boolean = false
}
