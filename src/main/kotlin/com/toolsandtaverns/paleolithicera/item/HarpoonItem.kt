package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiPacket
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
//? if >1.21.4
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
//? if >1.21.4
import java.util.function.Consumer

/**
 * Base class for harpoon items. Provides shared use logic and water validation.
 * Subclasses can tweak the balance via properties like durability and tooltips.
 */
open class HarpoonItem(settings: Settings) : Item(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        if (!world.isClient) {
            val hitResult = world.raycast(
                RaycastContext(
                    user.getCameraPosVec(1.0f),
                    user.getCameraPosVec(1.0f).add(user.getRotationVec(1.0f).multiply(5.0)),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.ANY,
                    user
                )
            )
            if (hitResult.type == HitResult.Type.BLOCK) {
                val blockPos = (hitResult as BlockHitResult).blockPos
                if (isValidWaterPatch(world, blockPos)) {
                    if (user is ServerPlayerEntity) {
                        val payload = OpenHarpoonGuiPacket.beginAttempt(user, this, hand)
                        ServerPlayNetworking.send(user, payload)
                    }
                    return ActionResult.SUCCESS
                }
            }
        }
        return ActionResult.PASS
    }

    protected open fun isValidWaterPatch(world: World, center: BlockPos): Boolean {
        for (dx in -1..1) {
            for (dz in -1..1) {
                val pos = center.add(dx, 0, dz)
                val fluid = world.getFluidState(pos)
                if (!fluid.isStill || !fluid.isOf(Fluids.WATER)) return false
            }
        }
        return true
    }

    @Deprecated("Overrides a deprecated method", level = DeprecationLevel.HIDDEN)
    //? if >1.21.4 {
    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        displayComponent: TooltipDisplayComponent,
        textConsumer: Consumer<Text>,
        type: TooltipType
    ) {
        if (type.isAdvanced && stack.isDamaged) {
            val durability = stack.maxDamage - stack.damage
            textConsumer.accept(
                Text.translatable("item.durability", durability, stack.maxDamage)
                    .formatted(Formatting.DARK_GRAY)
            )
        }
    }
    //?} else {
    /*override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        type: TooltipType
    ) {
        if (type.isAdvanced && stack.isDamaged) {
            val durability = stack.maxDamage - stack.damage
            tooltip.add(
                Text.translatable("item.durability", durability, stack.maxDamage)
                    .formatted(Formatting.DARK_GRAY)
            )
        }
    }*///?}
}

