package com.toolsandtaverns.paleolithicera.entity.ai.goal

import com.toolsandtaverns.paleolithicera.entity.IbexEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.MathHelper
import java.util.EnumSet
import kotlin.math.abs

/**
 * Short, committed ram toward current attacker/target. Tuned for hilly terrain:
 * - Slightly more lenient steering than boar
 * - Stops if vertical difference becomes too high (cliff edge safety)
 */
class IbexChargeAndRamGoal(
    private val ibex: IbexEntity,
    private val baseSpeed: Double,
    private val chargeSpeedMultiplier: Double
) : Goal() {

    private var target: LivingEntity? = null
    private var chargeTicks = 0
    private val maxChargeTicks = 20 * 2 // ~2 seconds commit

    init {
        controls = EnumSet.of(Control.MOVE, Control.LOOK)
    }

    override fun canStart(): Boolean {
        if (ibex.world.isClient) return false
        // Only ram when “alert” and has an attacker or recent aggressor from RevengeGoal
        val t = ibex.target ?: return false
        if (!ibex.isAlert()) return false
        target = t
        return ibex.squaredDistanceTo(t) < 20.0 // only close threats (<=5 blocks)
    }

    override fun start() {
        chargeTicks = 0
    }

    override fun shouldContinue(): Boolean {
        val t = target ?: return false
        if (chargeTicks >= maxChargeTicks) return false
        // If target is too high/low, abort to avoid yeeting off cliffs
        val dy = abs(ibex.y - t.y)
        return !ibex.world.isClient && t.isAlive && dy <= 2.5 && ibex.squaredDistanceTo(t) <= 64.0
    }

    override fun tick() {
        chargeTicks++
        val t = target ?: return

        ibex.lookControl.lookAt(t, 40f, 40f)

        // Move with a small steering bias so we can follow on slopes
        val speed = baseSpeed * chargeSpeedMultiplier
        ibex.navigation.startMovingTo(t.x, t.y, t.z, speed)

        val dist = ibex.distanceTo(t)
        if (dist <= 2.25f) {
            // Light headbutt: damage + knockback
            val serverWorld = ibex.world as? ServerWorld
            if (serverWorld != null) {
                t.damage(serverWorld, ibex.damageSources.mobAttack(ibex), 3.0f)
            }
            val yawRad = ibex.yaw * (Math.PI.toFloat() / 180f)
            val kbX = (-MathHelper.sin(yawRad) * 0.6f).toDouble()
            val kbZ = ( MathHelper.cos(yawRad) * 0.6f).toDouble()
            t.addVelocity(kbX, 0.35, kbZ)

            // short “cooldown”
            chargeTicks = maxChargeTicks
        }
    }

    override fun stop() {
        target = null
        ibex.navigation.stop()
    }
}
