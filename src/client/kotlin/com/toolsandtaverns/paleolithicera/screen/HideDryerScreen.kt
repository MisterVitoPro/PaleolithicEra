package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Identifier

/**
 * The GUI screen for the Hide Dryer block.
 * Shows input/output slots and a horizontal drying progress bar.
 */
class HideDryerScreen(
    handler: HideDryerScreenHandler,
    val playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<HideDryerScreenHandler>(handler, playerInventory, title) {

    companion object {
        val TEXTURE: Identifier = id("textures/gui/container/hide_dryer.png")
        private const val PROGRESS_BAR_WIDTH = 17
        private const val PROGRESS_BAR_HEIGHT = 10
        private const val PROGRESS_BAR_X = 78
        private const val PROGRESS_BAR_Y = 37
    }

    init {
        backgroundWidth = 176
        backgroundHeight = 166
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        // Draw background texture
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x, y,
            0f, 0f,
            backgroundWidth, backgroundHeight,
            256, 256)

        // Draw progress bar
        val progressWidth = handler.getScaledProgress(PROGRESS_BAR_WIDTH)
        if (progressWidth > 0) {
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + PROGRESS_BAR_X,
                y + PROGRESS_BAR_Y,
                176f, 0f, // source texture origin (U/V for progress bar)
                progressWidth, PROGRESS_BAR_HEIGHT,
                256, 256
            )
        }
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        super.drawForeground(context, mouseX, mouseY)
        context.drawText(textRenderer, title, titleX, titleY, Colors.DARK_GRAY, false)
        context.drawText(textRenderer, playerInventory.displayName, 8, backgroundHeight - 94, 0x404040, false)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta) // Draw the darkened background behind the GUI
        super.render(context, mouseX, mouseY, delta)      // Draw containers, slots, and items
        drawMouseoverTooltip(context, mouseX, mouseY)     // Draw tooltips when hovering over items
    }

    override fun shouldPause(): Boolean = false
}
