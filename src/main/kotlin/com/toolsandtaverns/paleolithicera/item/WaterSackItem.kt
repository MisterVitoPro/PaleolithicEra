package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.Items
import net.minecraft.potion.Potions
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import java.util.function.Predicate

class WaterSackItem(settings: Settings) : Item(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand?): ActionResult {
        val list = world.getEntitiesByClass(
            AreaEffectCloudEntity::class.java,
            user.boundingBox.expand(2.0),
            Predicate { entity: AreaEffectCloudEntity? -> entity != null && entity.isAlive && entity.owner is EnderDragonEntity })
        val itemStack = user.getStackInHand(hand)
        if (!list.isEmpty()) {
            val areaEffectCloudEntity = list[0] as AreaEffectCloudEntity
            areaEffectCloudEntity.radius = areaEffectCloudEntity.radius - 0.5f
            world.playSound(
                //? if >1.21.4 {
                null as Entity?,
                //?} else {
                /*null as PlayerEntity?,*///?}
                user.x,
                user.y,
                user.z,
                SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH,
                SoundCategory.NEUTRAL,
                1.0f,
                1.0f
            )
            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, user.pos)
            if (user is ServerPlayerEntity) {
                val serverPlayerEntity = user
                Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayerEntity, itemStack, areaEffectCloudEntity)
            }

            return ActionResult.SUCCESS.withNewHandStack(this.fill(itemStack, user, ItemStack(Items.DRAGON_BREATH)))
        } else {
            val blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY)
            if (blockHitResult.type == HitResult.Type.MISS) {
                return ActionResult.PASS
            } else {
                if (blockHitResult.type == HitResult.Type.BLOCK) {
                    val blockPos = blockHitResult.blockPos
                    //? if >1.21.4 {
                    if (!world.canEntityModifyAt(user, blockPos)) {
                    //?} else {
                    /*if (!world.canPlayerModifyAt(user, blockPos)) {*///?}
                        return ActionResult.PASS
                    }

                    if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                        world.playSound(
                            user,
                            user.x,
                            user.y,
                            user.z,
                            SoundEvents.ITEM_BOTTLE_FILL,
                            SoundCategory.NEUTRAL,
                            1.0f,
                            1.0f
                        )
                        world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos)
                        return ActionResult.SUCCESS.withNewHandStack(
                            this.fill(
                                itemStack,
                                user,
                                ModItems.FILLED_WATER_SACK.defaultStack
                            )
                        )
                    }
                }

                return ActionResult.PASS
            }
        }
    }

    private fun fill(stack: ItemStack?, player: PlayerEntity, outputStack: ItemStack?): ItemStack? {
        player.incrementStat(Stats.USED.getOrCreateStat(this))
        return ItemUsage.exchangeStack(stack, player, outputStack)
    }
}
