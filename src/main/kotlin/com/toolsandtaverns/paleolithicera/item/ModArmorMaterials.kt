package com.toolsandtaverns.paleolithicera.item

import com.google.common.collect.Maps
import com.toolsandtaverns.paleolithicera.item.equipment.ModEquipmentAssets
import com.toolsandtaverns.paleolithicera.registry.ModItemTags
import com.toolsandtaverns.paleolithicera.util.id
import com.toolsandtaverns.paleolithicera.util.regKeyOfItem
import net.minecraft.item.equipment.ArmorMaterial
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.sound.SoundEvents

object ModArmorMaterials {

    val RAWHIDE_MATERIAL: ArmorMaterial = ArmorMaterial(
        8, // Base durability (lower than leather)
        createDefenseMap(0, 1, 1, 0, 1), // No helmet/boots
        1, // Enchantability (extremely low)
        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
        0.0f, // Toughness
        0.0f, // Knockback resistance
        ModItemTags.REPAIRS_RAWHIDE_ARMOR, // Repair tag
        ModEquipmentAssets.RAWHIDE_EQUIPMENT_ASSET // Asset key
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


