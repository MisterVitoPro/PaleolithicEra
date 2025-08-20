package com.toolsandtaverns.paleolithicera.entity.ai.goal

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import java.util.*
import kotlin.random.Random

/**
 * Occasionally sets a player as the attack target when in range, based on [chance].
 * Does nothing while the entity is currently fleeing.
 */
class ChanceTargetGoal(
    private val animal: AnimalEntity,
    private val targetClass: Class<out LivingEntity>,
    private val chance: Double,
    private val range: Double,
    targetPredicate: TargetPredicate.EntityPredicate?
) : Goal() {

    private var target: LivingEntity? = null
    private val targetPredicate: TargetPredicate


    init {
        this.controls = EnumSet.of(Control.TARGET)
        this.targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate)
    }

    override fun canStart(): Boolean {
        if (Random.nextDouble() > chance) return false

        val serverWorld = getServerWorld(this.animal)

        val nearest = serverWorld.getClosestPlayer(this.getAndUpdateTargetPredicate(), this.animal, this.animal.x, this.animal.eyeY, this.animal.z)
        if (nearest != null && targetClass.isAssignableFrom(nearest.javaClass)) {
            target = nearest
            println("Target Acquired: $target")
            return true
        }
        return false
    }

    override fun shouldContinue(): Boolean {
        val t = animal.target
        return t != null && t.isAlive
    }

    /** set the target for our goal to pick up. */
    override fun start() {
        animal.target = target as? PlayerEntity
    }

    /** If aborted, clear any transient target. */
    override fun stop() {
        animal.target = null
        target = null
    }

    private fun getFollowRange(): Double {
        return this.animal.getAttributeValue(EntityAttributes.FOLLOW_RANGE)
    }

    private fun getAndUpdateTargetPredicate(): TargetPredicate? {
        return this.targetPredicate.setBaseMaxDistance(this.getFollowRange())
    }
}