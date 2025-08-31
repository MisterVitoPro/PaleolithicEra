package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.entity.SpearEntity
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

/**
 * Primitive throwable bone spear entity with no magical effects.
 */
class BoneSpearEntity : SpearEntity {

    /**
     * Thrown by a living owner with a specific item stack.
     */
    constructor(world: World, owner: LivingEntity, stack: ItemStack)
            : super(ModEntityType.BONE_SPEAR_ENTITY as EntityType<out SpearEntity>, owner, world, stack)

    /**
     * Engine constructor.
     */
    constructor(type: EntityType<out SpearEntity>, world: World)
            : super(type, world)

    // Bone tier: slightly better than wooden spear
    override val baseDamage: Float = 4.0f // Better damage than wooden spear
    override val slownessTickDuration: Int = 60 // 3s - longer debuff duration
    override val slownessAmplifier: Int = 1 // Slowness II instead of I

    override fun getDefaultItemStack(): ItemStack = ItemStack(ModItems.BONE_SPEAR)

}