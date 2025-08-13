package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

/**
 * Primitive throwable bone spear entity with no magical effects.
 */
class BoneSpearEntity : PersistentProjectileEntity {

    constructor(world: World, owner: LivingEntity, stack: ItemStack)
            : super(ModEntities.BONE_SPEAR_ENTITY, owner, world, stack, stack) {
        this.setNoGravity(false)
    }

    constructor(type: EntityType<out BoneSpearEntity>, world: World)
            : super(type, world)

    override fun onEntityHit(hitResult: EntityHitResult) {
        super.onEntityHit(hitResult)
        if (!this.world.isClient) {
            this.discard()
        }
    }

    override fun getHitSound(): SoundEvent? {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND
    }

    override fun getDefaultItemStack(): ItemStack = ItemStack(ModItems.BONE_SPEAR)

    override fun shouldRender(cameraX: Double, cameraY: Double, cameraZ: Double): Boolean {
        return true
    }

}