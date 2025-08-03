package com.toolsandtaverns.paleolithicera.item.equipment

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.item.equipment.EquipmentAsset
import net.minecraft.item.equipment.EquipmentAssetKeys
import net.minecraft.registry.RegistryKey

/**
 * Contains registry keys for custom equipment assets used in the Paleolithic Era mod.
 * 
 * Equipment assets define shared properties for item sets, allowing for consistent
 * behavior across multiple items that share the same material or crafting technique.
 * This is particularly important for the Paleolithic mod, where primitive materials
 * like hide, bone, and flint are used across multiple item types.
 * 
 * These registry keys are referenced during item registration and data generation
 * to ensure that equipment made from the same materials shares appropriate properties
 * like durability, repair ingredients, and protection values.
 */
object ModEquipmentAssets {

    /**
     * Registry key for hide-based equipment properties.
     * 
     * The hide equipment asset defines characteristics for all hide-based items, including:
     * - Moderate protection values (lower than metal but better than nothing)
     * - Low durability (reflecting the fragile nature of primitive hide processing)
     * - Appropriate repair ingredients (patches of hide)
     * - Visual and sound characteristics unique to primitive hide armor
     * 
     * Hide represents one of the earliest clothing technologies available in the
     * Paleolithic progression system, offering basic protection while reflecting
     * the limited processing capabilities of early humans.
     */
    val HIDE_EQUIPMENT_ASSET: RegistryKey<EquipmentAsset> = RegistryKey.of(
        EquipmentAssetKeys.REGISTRY_KEY,
        id("hide")
    )

}