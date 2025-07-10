package com.tootsandtaverns.paleolithicera.item

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.item.ToolMaterial
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModToolMaterials {

    val CRUDE_KNIFE_MATERIAL = ToolMaterial(
        BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Blocks it cannot mine properly
        35,                                  // Durability
        1.0f,                                // Mining speed
        1.0f,                                // Attack damage bonus
        5,                                   // Enchantability
        TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of(MOD_ID,"flint_tools")
        )
    )


}