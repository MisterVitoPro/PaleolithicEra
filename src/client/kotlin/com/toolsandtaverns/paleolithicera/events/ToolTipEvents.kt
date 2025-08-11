package com.toolsandtaverns.paleolithicera.events

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TooltipEvents {

    fun register() {
        ItemTooltipCallback.EVENT.register { stack, context, type, lines ->
            val id = Registries.ITEM.getId(stack.item)
            if (id.namespace != MOD_ID) return@register // Only our mod's items/blocks

            // Tooltip translation key format: tooltip.<modid>.<item_path>
            val tooltipKey = "tooltip.$MOD_ID.${id.path}"

            // Only add tooltip if translation exists
            if (Text.translatable(tooltipKey).string != tooltipKey) {
                lines.add(Text.translatable(tooltipKey).formatted(Formatting.GRAY))
            }

            // Optional: also check for extra "usage" line
            val usageKey = "$tooltipKey.usage"
            if (Text.translatable(usageKey).string != usageKey) {
                lines.add(Text.translatable(usageKey).formatted(Formatting.DARK_GREEN))
            }
        }
    }
}
