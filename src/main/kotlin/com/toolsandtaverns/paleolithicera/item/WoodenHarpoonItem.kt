package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
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
import java.util.function.Consumer

/**
 * A fishing tool that allows players to catch fish by playing a minigame.
 *
 * The harpoon can only be used when targeting a valid water patch.
 * When used, it opens a timing-based minigame where the player must press space
 * at the right moment to catch a fish.
 */
class WoodenHarpoonItem(settings: Settings) : Item(settings) {

    /**
     * Handles the right-click usage of the wooden harpoon.
     *
     * When a player right-clicks with this item while pointing at a valid water patch,
     * sends a packet to the client to open the harpoon fishing minigame screen.
     *
     * @param world The world in which the item is being used
     * @param user The player using the item
     * @param hand The hand in which the item is being held
     * @return SUCCESS if the action was handled, PASS otherwise
     */
    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        // Only handle this on the server side to prevent duplicate execution
        if (!world.isClient) {
            // Cast a ray from the player's camera position in the direction they're looking
            val hitResult = world.raycast(
                RaycastContext(
                    user.getCameraPosVec(1.0f), // Start at player's eye position
                    user.getCameraPosVec(1.0f).add(user.getRotationVec(1.0f).multiply(5.0)), // Extend 5 blocks
                    RaycastContext.ShapeType.OUTLINE, // Hit block outlines
                    RaycastContext.FluidHandling.ANY, // Allow hitting fluids
                    user
                )
            )
            // Check if the ray hit a block (not air or entity)
            if (hitResult.type == HitResult.Type.BLOCK) {
                val blockPos = (hitResult as BlockHitResult).blockPos
                // Check if the block is part of a valid 3x3 water patch
                if (isValidWaterPatch(world, blockPos)) {
                    // If the user is a server player, we can send them a network packet
                    if (user is ServerPlayerEntity) {
                        // Send a packet to open the fishing GUI on the client side
                        ServerPlayNetworking.send(user, OpenHarpoonGuiPayload)
                    }
                    return ActionResult.SUCCESS
                }
            }
        }
        return ActionResult.PASS
    }

    /**
     * Checks if a given block position is surrounded by a valid 3x3 patch of still water.
     *
     * This method verifies that the targeted block and all 8 adjacent blocks in the same
     * Y-level contain still water (not flowing). This ensures the player is fishing in
     * a proper body of water rather than a single water block or flowing water.
     *
     * @param world The world to check in
     * @param center The central block position to check around
     * @return true if a 3x3 area of still water exists, false otherwise
     */
    private fun isValidWaterPatch(world: World, center: BlockPos): Boolean {
        // Check a 3x3 grid centered on the target block
        for (dx in -1..1) {
            for (dz in -1..1) {
                val pos = center.add(dx, 0, dz) // Check blocks at same Y level
                val fluid = world.getFluidState(pos)
                // Ensure the fluid is still water (not flowing) at this position
                if (!fluid.isStill || !fluid.isOf(Fluids.WATER)) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Adds tooltip information to the wooden harpoon item.
     *
     * Displays a description of how to use the harpoon and, if advanced tooltips
     * are enabled (F3+H), shows the remaining durability of the item.
     *
     * @param stack The ItemStack being inspected
     * @param context The tooltip context
     * @param displayComponent The tooltip display component
     * @param textConsumer Consumer that accepts text lines to be added to the tooltip
     * @param type The tooltip type (basic or advanced)
     */
    @Deprecated("Overrides a deprecated method", level = DeprecationLevel.HIDDEN)
    @Environment(EnvType.CLIENT)
    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        displayComponent: TooltipDisplayComponent,
        textConsumer: Consumer<Text>,
        type: TooltipType
    ) {
        // If advanced tooltips are enabled and the item is damaged,
        // show remaining durability information
        if (type.isAdvanced && stack.isDamaged) {
            val durability = stack.maxDamage - stack.damage
            textConsumer.accept(
                Text.translatable("item.durability", durability, stack.maxDamage)
                    .formatted(Formatting.DARK_GRAY)
            )
        }
    }
}
