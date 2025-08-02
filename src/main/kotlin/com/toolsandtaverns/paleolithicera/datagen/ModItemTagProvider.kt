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

class ModItemTagProvider(output: FabricDataOutput, val registries: CompletableFuture<RegistryWrapper.WrapperLookup>)
    : FabricTagProvider<Item>(output, RegistryKeys.ITEM, registries) {

    override fun configure(arg: RegistryWrapper.WrapperLookup) {
        getTagBuilder(ModItemTags.BONE_TOOL_MATERIALS)
            .add(Registries.ITEM.getId(Items.BONE))
            .add(Registries.ITEM.getId(ModItems.BONE_KNIFE))
            .add(Registries.ITEM.getId(ModItems.BONE_SPEAR))

        getTagBuilder(ModItemTags.FLINT_TOOL_MATERIALS)
            .add(Registries.ITEM.getId(ModItems.FLINT_KNIFE))
            .add(Registries.ITEM.getId(Items.FLINT))

        getTagBuilder(ModItemTags.REPAIRS_HIDE_ARMOR)
            .add(Registries.ITEM.getId(ModItems.DRY_HIDE))
            .add(Registries.ITEM.getId(ModItems.PATCHED_HIDE))

        getTagBuilder(ModItemTags.SPEARS)
            .add(Registries.ITEM.getId(ModItems.WOODEN_SPEAR))
            .add(Registries.ITEM.getId(ModItems.BONE_SPEAR))

        getTagBuilder(ModItemTags.KNIFE)
            .add(Registries.ITEM.getId(ModItems.FLINT_BIFACE))
            .add(Registries.ITEM.getId(ModItems.BONE_KNIFE))
            .add(Registries.ITEM.getId(ModItems.FLINT_KNIFE))
    }
}
