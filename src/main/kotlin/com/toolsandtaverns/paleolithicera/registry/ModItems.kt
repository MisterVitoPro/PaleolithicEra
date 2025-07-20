package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.item.FireDrillItem
import com.toolsandtaverns.paleolithicera.item.KnifeItem
import com.toolsandtaverns.paleolithicera.item.ToolMaterialsMod
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
    val BARK: Item = register("bark", { settings: Item.Settings -> Item(settings) })
    val PLANT_FIBER: Item = register("plant_fiber", { settings: Item.Settings -> Item(settings) })
    val FLINT_SHARD: Item = register("flint_shard", { settings: Item.Settings -> Item(settings) })

    val BONE_KNIFE: Item = register("bone_knife", { settings: Item.Settings -> KnifeItem(settings, ToolMaterialsMod.BONE_MATERIAL) })
    val BONE_SPEAR: Item = register("bone_spear", { settings: Item.Settings -> TridentItem(settings) })
    val BONE_SPEARHEAD: Item = register("bone_spearhead", { settings: Item.Settings -> Item(settings) })
    val FLINT_KNIFE: Item = register(
        "flint_knife",
        { settings: Item.Settings -> KnifeItem(settings, ToolMaterialsMod.FLINT_MATERIAL) })
    val FIRE_DRILL: Item =
        register("fire_drill", { settings: Item.Settings -> FireDrillItem(settings.maxCount(1).maxDamage(10)) })

    val RAW_ELDERBERRIES = register("raw_elderberries") {
        FoodComponent.Builder()
            .hunger(2)
            .saturationModifier(0.1f)
            .statusEffect(StatusEffectInstance(StatusEffects.POISON, 60), 1.0f)
            .build()
    }

    val TAB_ICON_ITEM: Item = register("tab_icon", { settings: Item.Settings -> Item(settings.maxCount(1)) })

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