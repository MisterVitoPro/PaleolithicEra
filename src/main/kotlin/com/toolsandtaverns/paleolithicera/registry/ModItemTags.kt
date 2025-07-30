package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModItemTags {

    val BONE_TOOL_MATERIALS = of("bone_tool_materials")
    val FLINT_TOOL_MATERIALS = of("flint_tool_materials")

    val REPAIRS_RAWHIDE_ARMOR = of("repairs_rawhide_armor")

    val KNIFE = of("knife")
    val SPEARS = of("spears")

    /**
     * Creates an Identifier for item tags under the mod's namespace.
     */
    fun of(name: String): TagKey<Item> {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name))
    }

}