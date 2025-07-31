package com.toolsandtaverns.paleolithicera.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.client.gl.RenderPipelines

/**
 * Screen (GUI) for the Knapping Station block.
 * 
 * This screen displays a crafting interface that allows players to create
 * tools and materials by knapping (shaping) rocks and other materials.
 */
class KnappingStationScreen(
    handler: KnappingScreenHandler,
    val inventory: PlayerInventory,
    title: Text
) : HandledScreen<KnappingScreenHandler>(handler, inventory, title) {

    companion object {
        /** Texture file for the GUI background */
        private val TEXTURE = Identifier.of("paleolithic-era", "textures/gui/container/knapping_station.png")
    }

    init {
        // Set standard vanilla GUI dimensions
        backgroundWidth = 176
        backgroundHeight = 166
    }

    /**
     * Initializes the screen when it's created or resized.
     * 
     * Centers the title text in the screen's title bar area.
     */
    override fun init() {
        super.init()
        // Center the title text horizontally
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    /**
     * Draws the background texture of the GUI.
     * 
     * @param context Drawing context
     * @param delta Partial tick time for smooth animations
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     */
    override fun drawBackground(
        context: DrawContext,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        // Calculate the top-left position to center the GUI on screen
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2

        // Draw the background texture using the GUI render pipeline
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED, // Use the GUI texture pipeline for proper rendering
            TEXTURE,                      // The texture to draw
            x, y,                         // Screen position to draw at
            0f, 0f,                       // Starting UV coordinates in the texture
            backgroundWidth, backgroundHeight, // Width and height to draw
            256, 256                      // Total texture size (standard Minecraft UI texture size)
        )
    }

    /**
     * Renders the complete screen, including background, slots, and tooltips.
     * 
     * @param context Drawing context
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     * @param delta Partial tick time for smooth animations
     */
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta) // Draw the darkened background behind the GUI
        super.render(context, mouseX, mouseY, delta)      // Draw containers, slots, and items
        drawMouseoverTooltip(context, mouseX, mouseY)     // Draw tooltips when hovering over items
    }

    /**
     * Draws the foreground elements of the GUI, including title and inventory label.
     * 
     * @param context Drawing context
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     */
    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        super.drawForeground(context, mouseX, mouseY)
        // Draw the title at the top center (dark gray color)
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false)
        // Draw the player inventory label above the inventory slots
        context.drawText(textRenderer, inventory.displayName, 8, backgroundHeight - 94, 0x404040, false)
    }
}
