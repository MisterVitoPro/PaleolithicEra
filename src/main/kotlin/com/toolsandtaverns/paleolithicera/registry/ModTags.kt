package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.util.RegistryHelpers
import net.minecraft.block.Block
import net.minecraft.registry.tag.TagKey

object ModTags {

    object Blocks {
        val REQUIRES_SHOVEL: TagKey<Block> = RegistryHelpers.tagKeyOfBlock("requires_shovel")
        val UNBREAKABLE_TAG: TagKey<Block> = RegistryHelpers.tagKeyOfBlock("unbreakable_without_tool")
    }

}