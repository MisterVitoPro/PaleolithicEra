package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.block.BlockState
import net.minecraft.entity.AnimationState
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
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BoarEntity(entityType: EntityType<out AnimalEntity>, world: World) : AnimalEntity(entityType, world) {

    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    override fun isBreedingItem(stack: ItemStack): Boolean {
        return stack.isOf(ModItems.RAW_ELDERBERRIES)
    }

    override fun createChild(
        world: ServerWorld,
        entity: PassiveEntity
    ): PassiveEntity? {
        return ModEntities.BOAR_ENTITY.create(world, SpawnReason.BREEDING)
    }

    private fun updateAnimations() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40
            this.idleAnimationState.start(this.age)
        } else {
            --this.idleAnimationTimeout
        }
    }

    override fun tick() {
        super.tick()

        if (this.world.isClient()) {
            this.updateAnimations()
        }

    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.ENTITY_PIG_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_PIG_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_PIG_DEATH
    }

    override fun playStepSound(pos: BlockPos?, state: BlockState) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP)
    }

    override fun initGoals() {
        this.goalSelector.add(0, SwimGoal(this))
        this.goalSelector.add(1, EscapeDangerGoal(this, 1.25))
        this.goalSelector.add(2, AnimalMateGoal(this, 1.0))
        this.goalSelector.add(3, TemptGoal(this, 1.2, Ingredient.ofItems(ModItems.RAW_ELDERBERRIES), true))
        this.goalSelector.add(4, FollowParentGoal(this, 1.1))
        this.goalSelector.add(5, WanderAroundFarGoal(this, 1.0))
        this.goalSelector.add(6, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        this.goalSelector.add(7, LookAroundGoal(this))
    }

    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder? {
            return createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.FOLLOW_RANGE, 20.0)
                .add(EntityAttributes.TEMPT_RANGE, 12.0)
        }
    }
}