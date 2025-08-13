package com.toolsandtaverns.paleolithicera.item

import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.ToolComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ProjectileItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.consume.UseAction
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Position
import net.minecraft.world.World

class SpearItem(settings: Settings, private val projectileCreator: ProjectileEntity.ProjectileCreator<*>) : Item(settings),
    ProjectileItem {

    companion object {
        private const val BASE_ATTACK_SPEED = -2.9
        private const val THROW_SPEED = 2.5f

        fun createAttributeModifiers(material: ToolMaterial): AttributeModifiersComponent {
            return AttributeModifiersComponent.builder().add(
                EntityAttributes.ATTACK_DAMAGE,
                EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.attackDamageBonus + 1.5, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
            ).add(
                EntityAttributes.ATTACK_SPEED,
                EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, BASE_ATTACK_SPEED, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
            ).build()
        }

        fun createToolComponent(material: ToolMaterial): ToolComponent {
            return ToolComponent(mutableListOf(), material.speed, 2, false)
        }
    }

    override fun getUseAction(stack: ItemStack?): UseAction {
        return UseAction.SPEAR
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int {
        return 72000
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int): Boolean {
        if (user !is PlayerEntity) return false

        val chargeTime = this.getMaxUseTime(stack, user) - remainingUseTicks
        if (chargeTime < 10 || stack.willBreakNextUse()) return false

        val sound: RegistryEntry<SoundEvent> = SoundEvents.ITEM_TRIDENT_THROW as RegistryEntry

        user.incrementStat(Stats.USED.getOrCreateStat(this))
        if (world is ServerWorld) {
            stack.damage(1, user)
            val itemStack = stack.splitUnlessCreative(1, user)
            val spearEntity = ProjectileEntity.spawnWithVelocity(
                projectileCreator,
                world,
                itemStack,
                user,
                0.0f,
                THROW_SPEED,
                1.0f
            ) as PersistentProjectileEntity
            if (user.isInCreativeMode) {
                spearEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
            }
            world.playSoundFromEntity(null, spearEntity, sound.value(), SoundCategory.PLAYERS, 1.0f, 1.0f)
            return true
        }
        return false
    }

    override fun use(world: World?, user: PlayerEntity, hand: Hand?): ActionResult {
        val itemStack = user.getStackInHand(hand)
        if (itemStack.willBreakNextUse()) {
            return ActionResult.FAIL
        } else {
            user.setCurrentHand(hand)
            return ActionResult.CONSUME
        }
    }

    override fun createEntity(world: World, pos: Position, stack: ItemStack, direction: Direction?): ProjectileEntity {
        if (world !is ServerWorld) {
            throw IllegalStateException("Projectile can only be created on the server side")
        }

        val entity = projectileCreator.create(world, null, stack.copyWithCount(1))
        entity.setPosition(pos.x, pos.y, pos.z)

        if (entity is PersistentProjectileEntity) {
            entity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED
        }

        return entity
    }

}