package com.toolsandtaverns.paleolithicera.datagen.tag

import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Items
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

/**
 * Generates item tags for the Paleolithic Era mod, organizing items into functional categories.
 *
 * Item tags serve multiple critical purposes in the mod's design:
 *
 * 1. **Recipe Organization**: Tags like BONE_TOOL_MATERIALS and FLINT_TOOL_MATERIALS group
 *    related crafting ingredients, allowing recipes to accept any item from appropriate categories
 *
 * 2. **Tool Classification**: The KNIFE and SPEARS tags categorize primitive weapons and tools,
 *    enabling gameplay mechanics to interact with these items as functional groups
 *
 * 3. **Repair System**: Tags like REPAIRS_HIDE_ARMOR define which materials can be used to repair
 *    specific equipment types, simulating primitive maintenance techniques
 *
 * 4. **Progression Gating**: Tags help control which items can interact with specific crafting
 *    stations or block types, reinforcing the technological progression theme
 *
 * These tag groupings reflect the material-based nature of Paleolithic technology, where
 * tool function was primarily determined by the material used rather than complex manufacturing.
 */
class ModItemTagProvider(output: FabricDataOutput, val registries: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricTagProvider.ItemTagProvider(output, registries) {

    /**
     * Configures all item tags for the Paleolithic Era mod.
     *
     * Each tag group represents a functional category of items that share similar uses,
     * materials, or roles in the progression system. These groupings reflect the material-based
     * nature of Paleolithic technology and help organize the mod's crafting system.
     *
     * @param arg The registry wrapper lookup for accessing item registries
     */
    override fun configure(arg: RegistryWrapper.WrapperLookup) {
        // Bone-based materials represent one of the earliest readily available hard materials
        // used by Paleolithic humans for tools and weapons
        valueLookupBuilder(ModTags.Items.BONE_TOOL_MATERIALS)
            .add(Items.BONE)
            .add(ModItems.BONE_KNIFE)
            .add(ModItems.BONE_SPEAR)

        // Flint tools represent a technological advancement over bone, offering
        // sharper edges and more durable cutting surfaces
        valueLookupBuilder(ModTags.Items.FLINT_TOOL_MATERIALS)
            .add(ModItems.FLINT_KNIFE)
            .add(Items.FLINT)

        // Defines materials that can repair hide-based armor, simulating how
        // Paleolithic humans would patch damaged clothing with additional hide pieces
        valueLookupBuilder(ModTags.Items.REPAIRS_HIDE_ARMOR)
            .add(ModItems.DRY_HIDE)
            .add(ModItems.PATCHED_HIDE)

        // Spears were one of the most important hunting and defense tools in the Paleolithic era,
        // with variations made from different available materials
        valueLookupBuilder(ModTags.Items.SPEARS)
            .add(ModItems.WOODEN_SPEAR)
            .add(ModItems.BONE_SPEAR)
        
        // Add harpoons to combat family (optional categorization)
        // Keeping within existing scheme; no dedicated tag yet
        // Could be split later into a HARPOONS tag if needed

        // Knives were versatile tools used for everything from food preparation
        // to hide processing and crafting other tools
        valueLookupBuilder(ModTags.Items.KNIFE)
            .add(ModItems.FLINT_BIFACE)
            .add(ModItems.BONE_KNIFE)
            .add(ModItems.FLINT_KNIFE)

        valueLookupBuilder(ItemTags.PLANKS)
            .add(ModBlocks.WILLOW_PLANKS.asItem())
    }
}
