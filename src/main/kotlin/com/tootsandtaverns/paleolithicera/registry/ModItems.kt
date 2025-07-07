package com.tootsandtaverns.paleolithicera.registry

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.TridentItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier


object ModItems {
    val BONE_SPEAR: Item = register("bone_spear", { settings: Item.Settings -> TridentItem(settings) }, Item.Settings())
    val FLINT_SHARD : Item = register("flint_shard", { settings: Item.Settings -> Item(settings)}, Item.Settings())

    fun initialize() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
            .register(ModifyEntries { itemGroup: FabricItemGroupEntries -> itemGroup.add(BONE_SPEAR) })
    }

    fun register(name: String, itemFactory: (Item.Settings) -> Item, settings: Item.Settings): Item {
        // Create the item key.
        val itemKey = RegistryKey.of<Item>(RegistryKeys.ITEM, Identifier.of(MOD_ID, name))

        // Create the item instance.
        val item: Item = itemFactory(settings.registryKey(itemKey))

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item)

        return item
    }

}