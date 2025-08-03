package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.registry.ModItemTags
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
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
class ModItemTagProvider(output: FabricDataOutput, val registries: CompletableFuture<RegistryWrapper.WrapperLookup>)
    : FabricTagProvider<Item>(output, RegistryKeys.ITEM, registries) {

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
        getTagBuilder(ModItemTags.BONE_TOOL_MATERIALS)
            .add(Registries.ITEM.getId(Items.BONE))
            .add(Registries.ITEM.getId(ModItems.BONE_KNIFE))
            .add(Registries.ITEM.getId(ModItems.BONE_SPEAR))

        // Flint tools represent a technological advancement over bone, offering
        // sharper edges and more durable cutting surfaces
        getTagBuilder(ModItemTags.FLINT_TOOL_MATERIALS)
            .add(Registries.ITEM.getId(ModItems.FLINT_KNIFE))
            .add(Registries.ITEM.getId(Items.FLINT))

        // Defines materials that can repair hide-based armor, simulating how
        // Paleolithic humans would patch damaged clothing with additional hide pieces
        getTagBuilder(ModItemTags.REPAIRS_HIDE_ARMOR)
            .add(Registries.ITEM.getId(ModItems.DRY_HIDE))
            .add(Registries.ITEM.getId(ModItems.PATCHED_HIDE))

        // Spears were one of the most important hunting and defense tools in the Paleolithic era,
        // with variations made from different available materials
        getTagBuilder(ModItemTags.SPEARS)
            .add(Registries.ITEM.getId(ModItems.WOODEN_SPEAR))
            .add(Registries.ITEM.getId(ModItems.BONE_SPEAR))

        // Knives were versatile tools used for everything from food preparation
        // to hide processing and crafting other tools
        getTagBuilder(ModItemTags.KNIFE)
            .add(Registries.ITEM.getId(ModItems.FLINT_BIFACE))
            .add(Registries.ITEM.getId(ModItems.BONE_KNIFE))
            .add(Registries.ITEM.getId(ModItems.FLINT_KNIFE))
    }
}
