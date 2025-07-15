package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.block.Block
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.ToolMaterial
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

/**
 * Knife item that can strip logs and drop bark.
 * Does NOT extend AxeItem (to avoid full axe behavior).
 */
class KnifeItem(
    settings: Settings,
    material: ToolMaterial
) : AxeItem(material, 0.0f, -2.4f, settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val pos = context.blockPos
        val player = context.player ?: return ActionResult.PASS
        val state = world.getBlockState(pos)

        val stripped: Block = STRIPPED_BLOCKS[state.block] ?: return ActionResult.PASS

        if (!world.isClient) {
            // Replace with stripped block and preserve axis
            world.setBlockState(pos, stripped.defaultState.with(Properties.AXIS, state.get(Properties.AXIS)))

            // Drop bark
            val bark = Registries.ITEM.get(Identifier.of(MOD_ID, "bark"))
            Block.dropStack(world, pos, ItemStack(bark))

            // Play sound
            world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.2f)

            // Damage tool
            context.stack.damage(1, player, Hand.MAIN_HAND)
        }

        return ActionResult.SUCCESS
    }
}
