package com.toolsandtaverns.paleolithicera.entity.projectile

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

/**
 * Primitive throwable spear entity with no magical effects.
 * Functions like an arrow or trident, but breaks on impact.
 */
class WoodenSpearEntity : PersistentProjectileEntity {

    constructor(world: World, x: Double, y: Double, z: Double, stack: ItemStack) : super(
        ModEntities.SPEAR_ENTITY,
        x,
        y,
        z,
        world,
        stack,
        stack
    )

    constructor(world: World, owner: LivingEntity, stack: ItemStack)
            : super(ModEntities.SPEAR_ENTITY, owner, world, stack, stack) {
        this.setNoGravity(false)
    }

    constructor(type: EntityType<out WoodenSpearEntity>, world: World)
            : super(type, world)

    override fun onEntityHit(hitResult: EntityHitResult) {
        super.onEntityHit(hitResult)
        if (!this.world.isClient) {
            this.discard()
        }
    }

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, 3.toByte())
            this.discard()
        }
    }

    override fun getHitSound(): SoundEvent? {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND
    }

    override fun getDefaultItemStack(): ItemStack = ItemStack(ModItems.WOODEN_SPEAR)

    override fun shouldRender(cameraX: Double, cameraY: Double, cameraZ: Double): Boolean {
        return true
    }
}