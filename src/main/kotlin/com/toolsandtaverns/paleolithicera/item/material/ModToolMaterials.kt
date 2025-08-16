package com.toolsandtaverns.paleolithicera.item.material

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.item.ToolMaterial
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModToolMaterials {

    // Tier 0
    val BONE_MATERIAL = ToolMaterial(
        BlockTags.INCORRECT_FOR_WOODEN_TOOL,       // Blocks it cannot mine properly
        30,                                             // Durability
        0.5f,                                           // Mining speed
        1.0f,                                           // Attack damage bonus
        5,                                              // Enchantability
        TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of(MOD_ID, "bone_tool_material")
        )
    )

    // Tier 1
    val FLINT_MATERIAL = ToolMaterial(
        BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Blocks it cannot mine properly
        40,                                  // Durability
        1.0f,                                // Mining speed
        0.5f,                               // Attack damage bonus
        5,                                   // Enchantability
        TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of(MOD_ID, "flint_tool_material")
        )
    )


}