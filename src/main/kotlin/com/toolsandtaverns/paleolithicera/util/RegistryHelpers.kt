package com.toolsandtaverns.paleolithicera.util

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

fun id(path: String): Identifier {
    return Identifier.of(MOD_ID, path)
}

fun tagKeyOfBlock(name: String): TagKey<Block> {
    return TagKey.of(RegistryKeys.BLOCK, id(name))
}

fun regKeyOfBlock(name: String): RegistryKey<Block>? {
    return RegistryKey.of(RegistryKeys.BLOCK, id(name))
}

fun regKeyOfItem(name: String): RegistryKey<Item> {
    return RegistryKey.of(RegistryKeys.ITEM, id(name))
}