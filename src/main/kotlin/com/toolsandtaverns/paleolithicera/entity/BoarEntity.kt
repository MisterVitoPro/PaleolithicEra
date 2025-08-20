package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.entity.ai.goal.BoarChargeAndRamGoal
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import net.minecraft.block.BlockState
import net.minecraft.entity.AnimationState
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.SkeletonEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Boar with probabilistic aggression: may aggro players, performs a short charge + ram,
 * then flees quickly for a period.
 */
class BoarEntity(entityType: EntityType<out AnimalEntity>, world: World) : AnimalEntity(entityType, world) {

    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    /** How long (ticks) to keep fleeing after a successful hit. */
    var fleeDurationTicks: Int = 20 * 6 // 6 seconds
    /** Internal tick countdown for active fleeing behavior. */
    var fleeTicksRemaining: Int = 0

    /** Whether we just rammed something and should run. */
    var shouldFleeAfterAttack: Boolean = false

    override fun isBreedingItem(stack: ItemStack): Boolean {
        return stack.isOf(ModItems.getPlantItem(EdiblePlants.ELDERBERRY))
    }

    override fun createChild(world: ServerWorld, entity: PassiveEntity): PassiveEntity? {
        return ModEntityType.BOAR_ENTITY.create(world, SpawnReason.BREEDING)
    }

    /** Keeps the simple idle animation alive on clients. */
    private fun updateAnimations() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 50
            this.idleAnimationState.start(this.age)
        } else {
            --this.idleAnimationTimeout
        }
    }

    override fun tick() {
        super.tick()
        if (this.world.isClient) updateAnimations()

        // Countdown fleeing state if active.
        if (!world.isClient && fleeTicksRemaining > 0) {
            fleeTicksRemaining--
            if (fleeTicksRemaining <= 0) {
                shouldFleeAfterAttack = false
            }
        }
    }

    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_PIG_AMBIENT
    override fun getHurtSound(source: DamageSource): SoundEvent = SoundEvents.ENTITY_PIG_HURT
    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_PIG_DEATH
    override fun playStepSound(pos: BlockPos?, state: BlockState) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP)
    }

    override fun initGoals() {
        goalSelector.add(0, SwimGoal(this))
        goalSelector.add(3, AnimalMateGoal(this, 1.0))
        goalSelector.add(5, FollowParentGoal(this, 1.1))
        goalSelector.add(6, WanderAroundFarGoal(this, 1.0))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(2, ActiveTargetGoal(this, SkeletonEntity::class.java, true))
        this.initCustomGoals()
    }

    private fun initCustomGoals() {
        goalSelector.add(1, BoarChargeAndRamGoal(this, chargeSpeedMultiplier = 1.1))
        goalSelector.add(2, EscapeDangerGoal(this, 1.3))
        goalSelector.add(4, TemptGoal(this, 1.2, Ingredient.ofItems(ModItems.getPlantItem(EdiblePlants.ELDERBERRY)), true))
        targetSelector.add(1, ActiveTargetGoal(this, PlayerEntity::class.java, true) { entity, world ->
            entity is PlayerEntity && !entity.isCreative && entity.random.nextFloat() < 0.3f && world.isDay
        })
    }

    /**
     * Called by our charge goal after a successful attack to switch to flee behavior.
     * @param ticks how long to flee
     */
    fun triggerFlee(ticks: Int) {
        shouldFleeAfterAttack = true
        fleeTicksRemaining = ticks
    }

    companion object {
        /**
         * Base attributes for the boar. We slightly bump damage for a meaningful ram,
         * but keep health moderate.
         */
        fun createAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 12.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.TEMPT_RANGE, 12.0)
        }
    }
}
