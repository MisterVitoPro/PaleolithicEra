package com.toolsandtaverns.paleolithicera.entity

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
 * Primitive throwable spear entity with no magical effects.
 */
class WoodenSpearEntity : SpearEntity {

    /**
     * Thrown by a living owner with a specific item stack.
     */
    constructor(world: World, owner: LivingEntity, stack: ItemStack)
            : super(ModEntityType.WOODEN_SPEAR_ENTITY as EntityType<out SpearEntity>, owner, world, stack)

    /**
     * Engine constructor.
     */
    constructor(type: EntityType<out SpearEntity>, world: World)
            : super(type, world)

    // Wooden tier: a shorter slow feels right at the start of the game.
    override val slownessTickDuration: Int = 20 // 2s

    override fun getDefaultItemStack(): ItemStack = ItemStack(ModItems.WOODEN_SPEAR)

}