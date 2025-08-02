package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.util.tagKeyOfBlock
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

/**
 * Adds Paleolithic-specific block tags, including blocks that cannot be harvested without tools.
 */
class PaleolithicBlockTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        val unbreakableTag = tagKeyOfBlock("unbreakable_without_tool")

        builder(unbreakableTag)
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("spruce_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("birch_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("jungle_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("acacia_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("dark_oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mangrove_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("cherry_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("bamboo_block")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_spruce_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_birch_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_jungle_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_acacia_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_dark_oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_mangrove_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_cherry_log")))

    }

    override fun getName(): String = "Paleolithic Block Tags"
}
