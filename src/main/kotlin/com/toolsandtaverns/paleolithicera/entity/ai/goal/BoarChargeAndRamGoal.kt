package com.toolsandtaverns.paleolithicera.entity.ai.goal

import com.toolsandtaverns.paleolithicera.entity.BoarEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import java.util.EnumSet
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * A short, committed charge toward the current target. While charging, we
 * continuously refresh navigation if it goes idle. On contact, we melee + knockback,
 * then flip the boar into a flee state.
 */
class BoarChargeAndRamGoal(
    private val entity: BoarEntity,
    private val baseSpeed: Double = 1.0,
    private val chargeSpeedMultiplier: Double = 1.0
) : Goal() {

    private var target: LivingEntity? = null
    private var chargeTicks = 0
    private val maxChargeTicks = 40 // ~2s @ 20 tps
    private var repathTicker = 0

    init {
        // We control where to move and where to look during the charge.
        controls = EnumSet.of(Control.MOVE, Control.LOOK)
    }

    /** Only start on the server, with a live target, and when not fleeing. */
    override fun canStart(): Boolean {
        if (entity.world.isClient) return false
        if (entity.shouldFleeAfterAttack) return false

        val t = entity.target ?: return false
        if (!t.isAlive) return false

        target = t
        return true
    }

    /** Keep going while we have a live target, not fleeing, and under our max commit time. */
    override fun shouldContinue(): Boolean {
        val t = target
        return !entity.world.isClient && t != null && t.isAlive && !entity.shouldFleeAfterAttack && chargeTicks < maxChargeTicks
    }

    /** Reset per-run counters and visually indicate we’re sprinting. */
    override fun start() {
        chargeTicks = 0
        repathTicker = 0
        entity.isSprinting = true
    }

    /** Halt movement when the charge ends. */
    override fun stop() {
        target = null
        entity.isSprinting = false
        entity.navigation.stop()
    }

    /**
     * Drive movement toward the target every tick; if the pathfinder goes idle or
     * we've been running a bit, request a fresh path.
     */
    override fun tick() {
        val t = target ?: return
        chargeTicks++
        repathTicker++

        // Face the target.
        entity.lookControl.lookAt(t, 30f, 30f)

        // If we lost the path or it's been a moment, refresh a path to the target's coordinates.
        val speed = baseSpeed * chargeSpeedMultiplier
        if (entity.navigation.isIdle || repathTicker % 10 == 0) {
            entity.navigation.startMovingTo(t.x, t.y, t.z, speed)
        }

        // If we’re very close, hit + knockback and then flee.
        val distSq = entity.squaredDistanceTo(t)
        if (distSq <= 2.25) { // ~1.5 blocks radius window
            val world = entity.world
            if (world is ServerWorld) {


                if (entity.tryAttack(world, t)) {
                    // Small attack animation for clarity
                    entity.swingHand(Hand.MAIN_HAND, true)

                    // Directional knockback away from the entity’s facing.
                    val yawRad = Math.toRadians(entity.yaw.toDouble())
                    val dx = MathHelper.sin(yawRad.toFloat()).toDouble()
                    val dz = -MathHelper.cos(yawRad.toFloat()).toDouble()

                    val knockStrength = 1.4
                    t.takeKnockback(knockStrength, dx, dz)

                    // Transition into the flee state; another goal will take over movement.
                    entity.triggerFlee(entity.fleeDurationTicks)
                } else {
                    // If the vanilla hit failed (e.g., cooldown/angle), nudge closer aggressively.
                    nudgeForward(speed * 0.5)
                }
            }
        }
    }

    /** Micro-nudge forward in case nav briefly fails close to the target. */
    private fun nudgeForward(amount: Double) {
        val clamped = min(0.4, max(0.08, amount))
        val yawRad = Math.toRadians(entity.yaw.toDouble())
        entity.addVelocity(-sin(yawRad) * clamped, 0.0, cos(yawRad) * clamped)
        entity.velocityDirty = true
    }
}
