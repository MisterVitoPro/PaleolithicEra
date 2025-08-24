package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.entity.ai.goal.IbexChargeAndRamGoal
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModTags
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.tag.ItemTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.event.GameEvent

/**
 * Mountain ibex-like herbivore. Neutral; rams when provoked or crowded.
 * Behavior: wander steep terrain, avoid water, panic when on fire, breed via herb/food,
 * and short “ram” burst using a custom goal similar to the boar.
 */
class IbexEntity(type: EntityType<out AnimalEntity>, world: World) : AnimalEntity(type, world) {

    companion object {
        /** Build the base attribute set for this entity. */
        fun createAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 14.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(EntityAttributes.TEMPT_RANGE, 12.0)
        }

        /** Simple spawn rule: allow on mountain biomes, y >= 80, standard light checks. */
        fun canSpawn(
            type: EntityType<IbexEntity>,
            world: WorldAccess,
            reason: SpawnReason,
            pos: BlockPos,
            random: Random
        ): Boolean {
            val yOk = pos.y >= 80
            return yOk && isLightLevelValidForNaturalSpawn(world, pos)
        }
    }

    /** Tracks whether the ibex is “alert” (recently threatened). */
    private var alertTicks = 0

    override fun initGoals() {
        goalSelector.add(0, EscapeDangerGoal(this, 1.25))
        goalSelector.add(4, AnimalMateGoal(this, 1.0))
        goalSelector.add(5, WanderAroundFarGoal(this, 1.0))
        goalSelector.add(6, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(7, LookAroundGoal(this))

        targetSelector.add(1, RevengeGoal(this))
        this.initCustomGoals()
    }

    private fun initCustomGoals() {
        // “Ram when close to a threat” — mirrors your boar charge goal style.
        goalSelector.add(2, IbexChargeAndRamGoal(this, baseSpeed = 1.0, chargeSpeedMultiplier = 1.6))
        goalSelector.add(3, TemptGoal(this, 1.1, {stack -> stack.isIn(ModTags.Items.IBEX_FOOD)}, false))

    }

    override fun tick() {
        super.tick()
        // Ibex is alert and counting down the time before calming
        if (alertTicks > 0) alertTicks--
    }

    override fun playAmbientSound() {
        // Reuse goat ambient to bootstrap
        playSound(SoundEvents.ENTITY_GOAT_AMBIENT, 0.9f, 1.0f)
    }

    override fun getHurtSound(source: DamageSource): SoundEvent? = SoundEvents.ENTITY_GOAT_HURT

    override fun getDeathSound(): SoundEvent? = SoundEvents.ENTITY_GOAT_DEATH

    override fun damage(world: ServerWorld, source: DamageSource, amount: Float): Boolean {
        val ok = super.damage(world, source, amount)
        if (ok) {
            alertTicks = 20 * 6 // 6s alert window creates a bit of tension on mountains
            // Emit at this entity as the emitter per 1.21 API
            emitGameEvent(GameEvent.ENTITY_DAMAGE, this)
        }
        return ok
    }

    override fun isBreedingItem(stack: ItemStack): Boolean {
        return stack.isOf(ModItems.getPlantItem(EdiblePlants.YARROW)) || stack.isOf(Items.WHEAT)
    }

    override fun createChild(world: ServerWorld, entity: PassiveEntity): PassiveEntity? {
        return ModEntityType.IBEX_ENTITY.create(world, SpawnReason.BREEDING)
    }

    /** Whether the ibex is in an “alert” state (used by the ram goal). */
    fun isAlert(): Boolean = alertTicks > 0

}
