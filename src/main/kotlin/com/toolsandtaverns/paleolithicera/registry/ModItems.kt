package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.item.FireDrillItem
import com.toolsandtaverns.paleolithicera.item.ModToolMaterials
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
import java.util.function.Function


object ModItems {
    val BARK: Item = register("bark", { settings: Item.Settings -> Item(settings) })
    val BONE_SPEAR: Item = register("bone_spear", { settings: Item.Settings -> TridentItem(settings) })
    val BONE_SPEARHEAD: Item = register("bone_spearhead", { settings: Item.Settings -> Item(settings) })
    val CRUDE_KNIFE : Item = register("crude_knife", { settings: Item.Settings -> Item(settings.sword(ModToolMaterials.CRUDE_KNIFE_MATERIAL, 0.5f, -2.4f))})
    val FIRE_DRILL: Item = register("fire_drill", { settings: Item.Settings -> FireDrillItem(settings.maxCount(1).maxDamage(10)) })
    val FLINT_SHARD : Item = register("flint_shard", { settings: Item.Settings -> Item(settings)})
    val PLANT_FIBER : Item = register("plant_fiber", { settings: Item.Settings -> Item(settings)})

    fun initialize() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
            .register(ModifyEntries { itemGroup: FabricItemGroupEntries -> itemGroup.add(BONE_SPEAR) })
    }

    fun register(name: String, itemFactory: (Item.Settings) -> Item, settings: Item.Settings = Item.Settings()): Item {
        // Create the item key.
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name))

        // Create the item instance.
        val item: Item = itemFactory(settings.registryKey(itemKey))

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item)

        return item
    }

}