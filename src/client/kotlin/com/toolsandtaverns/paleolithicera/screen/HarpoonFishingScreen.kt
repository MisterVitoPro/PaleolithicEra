package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * A minigame screen for harpoon fishing that displays a moving slider.
 *
 * The player must press space when the slider is in the green target zone to catch a fish.
 * After a successful or failed attempt, the screen automatically closes after a short delay.
 */
class HarpoonFishingScreen : Screen(Text.literal("Fishing")) {
    // Position of the slider (0.0 to 1.0)
    private var sliderPos = 0f

    // Direction the slider is moving
    private var increasing = true

    // Range that represents a successful catch (45%-55% of the bar width)
    private val catchThreshold: ClosedFloatingPointRange<Float> = 0.45f..0.55f

    // Text shown after an attempt
    private var resultText: Text? = null

    // Whether the player has made an attempt
    private var resultSent = false

    // Counter for automatic screen closure
    private var ticksSinceResult = 0
    private val delayBeforeClose = 20 // 1 second at 20 TPS (ticks per second)

    /**
     * Renders the fishing minigame screen.
     *
     * Handles the automatic screen closure after a result, updates the slider position,
     * and draws the UI elements including the bar, target zone, and slider.
     *
     * @param context Drawing context used to render elements
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     * @param delta Time since last frame for smooth animations
     */
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        // Handle automatic screen closure after a result
        if (resultSent) {
            ticksSinceResult++
            if (ticksSinceResult >= delayBeforeClose) {
                client?.setScreen(null) // Close the screen after delay
            }
        }

        // Update slider position and handle direction changes at boundaries
        sliderPos += if (increasing) 0.01f else -0.01f
        if (sliderPos >= 1f) {
            sliderPos = 1f
            increasing = false // Reverse direction at right edge
        } else if (sliderPos <= 0f) {
            sliderPos = 0f
            increasing = true // Reverse direction at left edge
        }

        // Calculate positions for UI elements
        val screenCenter = this.width / 2
        val barWidth = 100
        val barX = screenCenter - barWidth / 2
        val sliderX = barX + (sliderPos * barWidth).toInt()

        // Draw the background bar (gray)
        context.fill(barX, 100, barX + barWidth, 110, 0xFFAAAAAA.toInt())
        // Draw the target zone (green)
        val greenStart = barX + (catchThreshold.start * barWidth).toInt()
        val greenEnd = barX + (catchThreshold.endInclusive * barWidth).toInt()
        context.fill(greenStart, 100, greenEnd, 110, 0xFF00FF00.toInt())
        // Draw the slider indicator (red)
        context.fill(sliderX - 1, 98, sliderX + 1, 112, 0xFFFF0000.toInt())

        // Draw result text if available
        context.drawCenteredTextWithShadow(textRenderer, resultText ?: Text.empty(), screenCenter, 130, 0xFFFFFF)
    }

    /**
     * Handles key press events for the fishing minigame.
     *
     * When the player presses the space bar, checks if the slider is in the target zone,
     * sends the result to the server, and updates the UI accordingly.
     *
     * @param keyCode The key code of the pressed key
     * @param scanCode System-specific scan code
     * @param modifiers Bit field describing which modifier keys were held down
     * @return Whether the key press was handled
     */
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (!resultSent && keyCode == 32) { // 32 is the key code for SPACE
            // Check if the slider is in the target zone
            val success = sliderPos in catchThreshold
            // Send the result to the server for processing rewards
            OpenHarpoonGuiClient.sendResult(success)
            // Update the UI with result text
            resultText = if (success) Text.literal("Caught a fish!") else Text.literal("The fish got away...")
            // Mark that we've sent a result to prevent multiple attempts
            resultSent = true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}
