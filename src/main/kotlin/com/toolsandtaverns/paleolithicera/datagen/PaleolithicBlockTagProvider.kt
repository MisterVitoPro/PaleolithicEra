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
 * Generates block tags that implement Paleolithic-era technology limitations and gameplay mechanics.
 * 
 * This tag provider is central to implementing several core gameplay concepts of the mod:
 * 
 * 1. **Tool Requirement System**: The 'unbreakable_without_tool' tag creates a realistic
 *    progression gate where certain materials like logs cannot be harvested by hand, requiring
 *    players to first craft appropriate primitive tools
 * 
 * 2. **Material Accessibility**: By controlling which blocks require tools, this system shapes
 *    the early game progression path and encourages tool crafting as a first priority
 * 
 * 3. **Historical Accuracy**: Reflects the reality that Paleolithic humans couldn't harvest
 *    certain materials without appropriate tools, creating a more authentic primitive
 *    technology experience
 * 
 * 4. **Progression Design**: Creates a natural technology tree where players must first
 *    gather accessible materials (sticks, stones) to craft tools that unlock access to
 *    more advanced resources (logs, structured wood)
 * 
 * These block tags fundamentally alter the vanilla Minecraft experience to better simulate
 * the technological constraints and progression of Paleolithic-era survival.
 */
class PaleolithicBlockTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    /**
     * Configures all block tags for the Paleolithic Era mod.
     * 
     * This method populates the 'unbreakable_without_tool' tag with all blocks that
     * require tools to harvest, creating a core gameplay mechanic that forces players
     * to follow a realistic technology progression path from the very beginning.
     * 
     * The selected blocks (primarily logs) represent materials that Paleolithic humans
     * would not have been able to effectively harvest or process without appropriate tools.
     * This creates an authentic primitive technology progression where tool crafting
     * is a necessary first step before structured building becomes possible.
     *
     * @param wrapperLookup Registry wrapper lookup for accessing block registries
     */
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        val unbreakableTag = tagKeyOfBlock("unbreakable_without_tool")

        // All log types are added to the unbreakable tag, as harvesting trees without tools
        // would have been impossible for Paleolithic humans. This creates an important
        // progression gate where players must craft primitive axes before accessing wood in quantity.
        builder(unbreakableTag)
            // Natural logs require tools to harvest
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("spruce_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("birch_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("jungle_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("acacia_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("dark_oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("mangrove_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("cherry_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("bamboo_block")))
            // Stripped logs are also unbreakable without tools, as they still represent substantial wood
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_spruce_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_birch_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_jungle_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_acacia_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_dark_oak_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_mangrove_log")))
            .add(RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla("stripped_cherry_log")))

    }

    /**
     * Provides a descriptive name for this tag provider in logs and reports.
     * 
     * The name clearly identifies that these generated tags are related to the
     * Paleolithic Era mod's block behavior modifications, helping with debugging
     * and organization during the data generation process.
     *
     * @return The name of this tag provider
     */
    override fun getName(): String = "Paleolithic Block Tags"
}
