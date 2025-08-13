package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text

object ModItemGroups {
    val PALEO_TAB_KEY: RegistryKey<net.minecraft.item.ItemGroup> =
        RegistryKey.of(RegistryKeys.ITEM_GROUP, id("paleolithic_era"))

    fun register() {
        Registry.register(
            Registries.ITEM_GROUP,
            PALEO_TAB_KEY.value,
            FabricItemGroup.builder()
                .icon { ItemStack(ModItems.TAB_ICON_ITEM) }
                .displayName(Text.translatable("itemGroup.$MOD_ID.paleolithic_era"))
                .entries { _, entries ->

                    // Collect all item IDs that belong to this mod's namespace
                    val allModItemIds = Registries.ITEM.ids
                        .filter { it.namespace == MOD_ID }
                        .sortedBy { it.path } // stable, readable ordering

                    // skip icon
                    val skip = setOf("tab_icon")

                    // Add everything else from the mod namespace
                    allModItemIds
                        .filter { it.path !in skip /* && it.path !in pinFirst */ }
                        .forEach { id ->
                            val item: Item = Registries.ITEM.get(id)
                            entries.add(item)
                        }
                }
                .build()
        )
    }
}
