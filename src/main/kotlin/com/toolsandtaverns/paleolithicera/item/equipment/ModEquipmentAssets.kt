package com.toolsandtaverns.paleolithicera.item.equipment

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.item.equipment.EquipmentAsset
import net.minecraft.item.equipment.EquipmentAssetKeys
import net.minecraft.registry.RegistryKey

object ModEquipmentAssets {

    val HIDE_EQUIPMENT_ASSET: RegistryKey<EquipmentAsset> = RegistryKey.of(
        EquipmentAssetKeys.REGISTRY_KEY,
        id("hide")
    )

}