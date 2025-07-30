package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.LeavesBlock
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.ToolMaterial
import net.minecraft.util.ActionResult

/**
 * Knife item that can strip logs to drop bark.
 */
class KnifeItem(
    material: ToolMaterial,
    attackDamage: Float,
    settings: Settings
) : AxeItem(material, attackDamage, -2.2f, settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val result: ActionResult = super.useOnBlock(context)
        if(result == ActionResult.SUCCESS){
            Block.dropStack(context.world, context.blockPos, ItemStack(ModItems.BARK))
        }
        return result
    }

    override fun getMiningSpeed(stack: ItemStack, state: BlockState): Float {
        return if (state.block is LeavesBlock) {
            5.0f // Adjust this value for speed. Vanilla shears are 15.0f
        } else {
            super.getMiningSpeed(stack, state)
        }
    }
}
