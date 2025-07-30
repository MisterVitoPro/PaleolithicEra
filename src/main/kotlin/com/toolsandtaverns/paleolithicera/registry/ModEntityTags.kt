package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ModEntityTags {

    val HUNTABLE_TAG: TagKey<EntityType<*>> =
        TagKey.of(RegistryKeys.ENTITY_TYPE, id("huntable"))

}