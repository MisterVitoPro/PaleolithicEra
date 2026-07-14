package com.toolsandtaverns.paleolithicera.screen

import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiClient
import com.toolsandtaverns.paleolithicera.network.HarpoonMinigameRules
import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Colors

/**
 * A minigame screen for harpoon fishing that displays a moving slider.
 *
 * The player must press space when the slider is in the green target zone to catch a fish.
 * After a successful or failed attempt, the screen automatically closes after a short delay.
 */
class HarpoonFishingScreen(
    private val challenge: OpenHarpoonGuiPayload
) : Screen(Text.translatable("screen.paleolithic-era.wooden_harpoon.title")) {
    // Position of the slider (0.0 to 1.0)
    private var sliderPos = 0f

    // Server-selected range that represents a successful catch.
    private val catchThreshold: ClosedFloatingPointRange<Float> = (
        challenge.targetStartStep.toFloat() / HarpoonMinigameRules.SLIDER_MAX_STEP
    )..(
        (challenge.targetStartStep + HarpoonMinigameRules.TARGET_WIDTH_STEPS).toFloat() /
            HarpoonMinigameRules.SLIDER_MAX_STEP
    )

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

        // Calculate positions for UI elements
        val screenCenter = this.width / 2
        val barWidth = 100
        val barX = screenCenter - barWidth / 2
        val sliderX = barX + (sliderPos * barWidth).toInt()

        // Draw the background bar (gray)
        context.fill(barX, 100, barX + barWidth, 110, Colors.LIGHT_GRAY)
        // Draw the target zone (green)
        val greenStart = barX + (catchThreshold.start * barWidth).toInt()
        val greenEnd = barX + (catchThreshold.endInclusive * barWidth).toInt()
        context.fill(greenStart, 100, greenEnd, 110, Colors.GREEN)
        // Draw the slider indicator (red)
        context.fill(sliderX - 1, 98, sliderX + 1, 112, Colors.RED)
    }

    /** Advances the minigame at a stable 20 updates per second, independent of frame rate. */
    override fun tick() {
        super.tick()

        if (resultSent) {
            ticksSinceResult++
            if (ticksSinceResult >= delayBeforeClose) {
                client?.setScreen(null)
            }
            return
        }

        val worldTick = client?.world?.time ?: challenge.startTick
        val elapsedTicks = (worldTick - challenge.startTick).coerceAtLeast(0L)
        sliderPos = HarpoonMinigameRules.sliderStepAt(elapsedTicks).toFloat() /
            HarpoonMinigameRules.SLIDER_MAX_STEP
    }

    /**
     * Handles key press events for the fishing minigame.
     *
     * When the player presses the space bar, sends the server-issued attempt identifier.
     * The server checks its own clock and target zone, then awards the result.
     *
     * @param keyCode The key code of the pressed key
     * @param scanCode System-specific scan code
     * @param modifiers Bit field describing which modifier keys were held down
     * @return Whether the key press was handled
     */
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val challengeStarted = (client?.world?.time ?: Long.MIN_VALUE) >= challenge.startTick
        if (!resultSent && challengeStarted && keyCode == 32) { // 32 is the key code for SPACE
            // The server validates the strike against its authoritative clock.
            OpenHarpoonGuiClient.sendResult(challenge.attemptId)
            // Mark that we've sent a result to prevent multiple attempts
            resultSent = true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}
