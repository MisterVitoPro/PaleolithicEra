package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.entity.WoodenSpearEntity
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Position
import net.minecraft.world.World

/**
 * Wooden Spear item, based on TridentItem for throwable and melee behavior.
 */
class WoodenSpearItem(settings: Settings) : TridentItem(
    settings
        .maxDamage(45) // Much lower than vanilla trident (250)
        .attributeModifiers(
            AttributeModifiersComponent.builder()
                .add(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributeModifier(
                        BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        1.0,
                        EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                )
                .add(
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributeModifier(
                        BASE_ATTACK_SPEED_MODIFIER_ID,
                        -2.8,
                        EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                )
                .build()
        )
) {

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int): Boolean {
        if (user !is PlayerEntity) return false

        val useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks
        if (useTicks < 10) return false

        if (stack.willBreakNextUse()) return false

        if (!world.isClient) {
            val spear = WoodenSpearEntity(world, user, stack.splitUnlessCreative(1, user))
            spear.setVelocity(user, user.pitch, user.yaw, 0.0f, 2.5f, 1.0f)
            spear.pickupType = if (user.abilities.creativeMode)
                PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
            else
                PersistentProjectileEntity.PickupPermission.ALLOWED

            world.spawnEntity(spear)
            world.playSoundFromEntity(null, spear, SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 1.0f, 1.0f)
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this))
        return true
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        val stack = user.getStackInHand(hand)
        if (stack.willBreakNextUse()) return ActionResult.FAIL
        user.setCurrentHand(hand)
        return ActionResult.CONSUME
    }


    override fun createEntity(world: World, pos: Position, stack: ItemStack, direction: Direction): ProjectileEntity {
        val spearEntity = WoodenSpearEntity(world, pos.x, pos.y, pos.z, stack.copyWithCount(1))
        spearEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED
        return spearEntity
    }

}