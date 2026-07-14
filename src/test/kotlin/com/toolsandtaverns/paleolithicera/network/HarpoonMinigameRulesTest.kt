package com.toolsandtaverns.paleolithicera.network

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("p0")
@Tag("unit")
class HarpoonMinigameRulesTest {
    @Test
    fun `slider follows a deterministic triangle wave`() {
        assertEquals(0, HarpoonMinigameRules.sliderStepAt(0))
        assertEquals(50, HarpoonMinigameRules.sliderStepAt(50))
        assertEquals(100, HarpoonMinigameRules.sliderStepAt(100))
        assertEquals(50, HarpoonMinigameRules.sliderStepAt(150))
        assertEquals(0, HarpoonMinigameRules.sliderStepAt(200))
    }

    @Test
    fun `server accepts strikes inside its target window`() {
        assertTrue(
            HarpoonMinigameRules.isSuccessful(targetStartStep = 45, startTick = 1_000, resultTick = 1_045)
        )
        assertTrue(
            HarpoonMinigameRules.isSuccessful(targetStartStep = 45, startTick = 1_000, resultTick = 1_055)
        )
        assertFalse(
            HarpoonMinigameRules.isSuccessful(targetStartStep = 45, startTick = 1_000, resultTick = 1_040)
        )
    }

    @Test
    fun `server allows bounded packet latency but rejects early strikes`() {
        val targetStart = 45
        val startTick = 1_000L
        val targetEndTick = startTick + targetStart + HarpoonMinigameRules.TARGET_WIDTH_STEPS

        assertTrue(
            HarpoonMinigameRules.isSuccessful(
                targetStart,
                startTick,
                targetEndTick + HarpoonMinigameRules.INPUT_GRACE_TICKS
            )
        )
        assertFalse(
            HarpoonMinigameRules.isSuccessful(
                targetStart,
                startTick,
                targetEndTick + HarpoonMinigameRules.INPUT_GRACE_TICKS + 1
            )
        )
        assertFalse(HarpoonMinigameRules.isSuccessful(targetStart, startTick, startTick - 1))
    }
}
