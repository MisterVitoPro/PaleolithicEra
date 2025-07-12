package com.toolsandtaverns.paleolithicera.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import com.toolsandtaverns.paleolithicera.screen.KnappingScreenHandler
import net.minecraft.client.gl.RenderPipelines

class KnappingStationScreen(
    handler: KnappingScreenHandler,
    val inventory: PlayerInventory,
    title: Text
) : HandledScreen<KnappingScreenHandler>(handler, inventory, title) {

    companion object {
        private val TEXTURE = Identifier.of("paleolithic-era", "textures/gui/container/knapping_station.png")
    }

    init {
        backgroundWidth = 176
        backgroundHeight = 166
    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    override fun drawBackground(
        context: DrawContext,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2

        // Use built-in GUI pipeline
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED, // required RenderPipeline
            TEXTURE,
            x, y,
            0f, 0f,
            backgroundWidth, backgroundHeight,
            256, 256
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        super.drawForeground(context, mouseX, mouseY)
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false)
        context.drawText(textRenderer, inventory.displayName, 8, backgroundHeight - 94, 0x404040, false)
    }
}
