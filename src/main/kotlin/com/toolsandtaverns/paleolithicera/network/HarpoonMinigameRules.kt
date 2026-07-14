package com.toolsandtaverns.paleolithicera.network

/** Shared deterministic rules used to render and validate the harpoon minigame. */
object HarpoonMinigameRules {
    const val SLIDER_MAX_STEP = 100
    const val TARGET_WIDTH_STEPS = 10
    const val INPUT_GRACE_TICKS = 4L

    private const val FULL_CYCLE_TICKS = SLIDER_MAX_STEP * 2L

    fun sliderStepAt(elapsedTicks: Long): Int {
        if (elapsedTicks <= 0L) return 0

        val cycleTick = elapsedTicks % FULL_CYCLE_TICKS
        return if (cycleTick <= SLIDER_MAX_STEP) {
            cycleTick.toInt()
        } else {
            (FULL_CYCLE_TICKS - cycleTick).toInt()
        }
    }

    fun isSuccessful(targetStartStep: Int, startTick: Long, resultTick: Long): Boolean {
        require(targetStartStep in 0..(SLIDER_MAX_STEP - TARGET_WIDTH_STEPS))
        if (resultTick < startTick) return false

        val elapsedTicks = resultTick - startTick
        val target = targetStartStep..(targetStartStep + TARGET_WIDTH_STEPS)
        return (0L..INPUT_GRACE_TICKS).any { latencyTicks ->
            elapsedTicks >= latencyTicks && sliderStepAt(elapsedTicks - latencyTicks) in target
        }
    }
}
