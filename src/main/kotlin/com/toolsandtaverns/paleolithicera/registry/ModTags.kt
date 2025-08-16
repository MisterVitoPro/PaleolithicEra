package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.util.RegistryHelpers
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModTags {

    object Blocks {
        val REQUIRES_SHOVEL: TagKey<Block> = RegistryHelpers.tagKeyOfBlock("requires_shovel")
        val UNBREAKABLE_TAG: TagKey<Block> = RegistryHelpers.tagKeyOfBlock("unbreakable_without_tool")
    }

    object Entity {
        val HUNTABLE_TAG: TagKey<EntityType<*>> =
            TagKey.of(RegistryKeys.ENTITY_TYPE, id("huntable"))
    }

    object Items {
        val BONE_TOOL_MATERIALS = of("bone_tool_materials")
        val FLINT_TOOL_MATERIALS = of("flint_tool_materials")

        val REPAIRS_HIDE_ARMOR = of("repairs_hide_armor")

        val KNIFE = of("knife")
        val SPEARS = of("spears")

        /**
         * Creates an Identifier for item tags under the mod's namespace.
         */
        fun of(name: String): TagKey<Item> {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name))
        }
    }

}