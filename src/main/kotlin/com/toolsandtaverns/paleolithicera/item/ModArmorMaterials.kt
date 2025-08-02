package com.toolsandtaverns.paleolithicera.item

import com.google.common.collect.Maps
import com.toolsandtaverns.paleolithicera.item.equipment.ModEquipmentAssets
import com.toolsandtaverns.paleolithicera.registry.ModItemTags
import net.minecraft.item.equipment.ArmorMaterial
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.sound.SoundEvents

object ModArmorMaterials {

    val HIDE_MATERIAL: ArmorMaterial = ArmorMaterial(
        5, // Base durability (lower than leather)
        createDefenseMap(1, 1, 2, 1, 2), // No helmet/boots
        3, // Enchantability (extremely low)
        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
        0.0f, // Toughness
        0.0f, // Knockback resistance
        ModItemTags.REPAIRS_HIDE_ARMOR, // Repair tag
        ModEquipmentAssets.HIDE_EQUIPMENT_ASSET // Asset key
    )

    private fun createDefenseMap(
        boots: Int,
        legs: Int,
        chest: Int,
        helmet: Int,
        body: Int
    ): Map<EquipmentType, Int> {
        return Maps.newEnumMap(
            mapOf(
                EquipmentType.BOOTS to boots,
                EquipmentType.LEGGINGS to legs,
                EquipmentType.CHESTPLATE to chest,
                EquipmentType.HELMET to helmet,
                EquipmentType.BODY to body
            )
        )
    }
}


