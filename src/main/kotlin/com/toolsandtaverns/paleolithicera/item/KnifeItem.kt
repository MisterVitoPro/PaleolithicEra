package com.toolsandtaverns.paleolithicera.item

import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.block.Block
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.ToolMaterial
import net.minecraft.util.ActionResult

/**
 * Knife item that can strip logs to drop bark.
 */
class KnifeItem(
    settings: Settings,
    material: ToolMaterial
) : AxeItem(material, 0.5f, -2.2f, settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val result: ActionResult = super.useOnBlock(context)
        if(result == ActionResult.SUCCESS){
            Block.dropStack(context.world, context.blockPos, ItemStack(ModItems.BARK))
        }
        return result
    }
}
