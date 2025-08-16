package com.toolsandtaverns.paleolithicera.util

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

fun id(path: String): Identifier {
    return Identifier.of(MOD_ID, path)
}

object RegistryHelpers {
    fun tagKeyOfBlock(name: String): TagKey<Block> {
        return TagKey.of(RegistryKeys.BLOCK, id(name))
    }

    fun regKeyOfBlock(name: String): RegistryKey<Block> {
        return RegistryKey.of(RegistryKeys.BLOCK, id(name))
    }

    fun regKeyOfBlockVanilla(block: Block): RegistryKey<Block> {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla(Registries.BLOCK.getId(block).path))
    }
}

fun regKeyOfItem(name: String): RegistryKey<Item> {
    return RegistryKey.of(RegistryKeys.ITEM, id(name))
}

fun regKeyOfEntityType(name: String): RegistryKey<EntityType<*>?>? {
    return RegistryKey.of(RegistryKeys.ENTITY_TYPE, id(name))
}