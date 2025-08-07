package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.entity.WoodenSpearEntity
import com.toolsandtaverns.paleolithicera.item.*
import com.toolsandtaverns.paleolithicera.item.ModArmorMaterials.HIDE_MATERIAL
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ConsumableComponent
import net.minecraft.component.type.FoodComponent
import net.minecraft.component.type.WeaponComponent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.*
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import net.minecraft.item.consume.UseAction
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * Registry for all custom items in the Paleolithic Era mod.
 * 
 * This object handles the registration of all items, including:
 * - Basic materials and resources
 * - Tools and weapons
 * - Armor
 * - Food items
 * 
 * Each item is registered with the appropriate settings and factory method.
 */
object ModItems {
    val BARK: Item = register("bark", { settings: Item.Settings -> Item(settings) })
    val PLANT_FIBER: Item = register("plant_fiber", { settings: Item.Settings -> Item(settings) })
    val PLANT_CORDAGE: Item = register("plant_cordage", { settings: Item.Settings -> Item(settings) })
    val ROCK_CHUNK: Item = register("rock_chunk", { settings: Item.Settings -> Item(settings) })
    val RAWHIDE: Item = register("rawhide", { settings: Item.Settings -> Item(settings) })
    val DRY_HIDE: Item = register("dry_hide", { settings: Item.Settings -> Item(settings) })
    val PATCHED_HIDE: Item = register("patched_hide", { settings: Item.Settings -> Item(settings) })

    val HIDE_CAP : Item = register("hide_cap", { settings: Item.Settings ->
        Item(settings.armor(HIDE_MATERIAL, EquipmentType.HELMET))
    })
    val HIDE_TUNIC: Item = register("hide_tunic", { settings: Item.Settings ->
        Item(settings.armor(HIDE_MATERIAL, EquipmentType.CHESTPLATE))
    })
    val HIDE_LEGGINGS: Item = register("hide_leggings", { settings: Item.Settings ->
        Item(settings.armor(HIDE_MATERIAL, EquipmentType.LEGGINGS))
    })
    val HIDE_SHOES : Item = register("hide_shoes", { settings: Item.Settings ->
        Item(settings.armor(HIDE_MATERIAL, EquipmentType.BOOTS))
    })

    val WOODEN_SPEAR = register("wooden_spear",
        { settings: Item.Settings -> SpearItem( settings.maxDamage(ToolMaterial.WOOD.durability)
            .attributeModifiers(SpearItem.createAttributeModifiers(ToolMaterial.WOOD))
            .component(DataComponentTypes.TOOL, SpearItem.createToolComponent(ToolMaterial.WOOD))
            .enchantable(ToolMaterial.WOOD.enchantmentValue())
            .component(DataComponentTypes.WEAPON, WeaponComponent(1)), ::WoodenSpearEntity) }

    )

    val FLINT_BIFACE: Item = register("flint_biface", { settings: Item.Settings -> KnifeItem(ToolMaterialsMod.FLINT_MATERIAL, settings.maxDamage(15), 0.0f) })
    val BONE_KNIFE: Item = register("bone_knife", { settings: Item.Settings -> KnifeItem(ToolMaterialsMod.BONE_MATERIAL, settings) })
    val BONE_SPEAR: Item = register("bone_spear", { settings: Item.Settings -> TridentItem(settings) })
    val BONE_SHARD: Item = register("bone_shard", { settings: Item.Settings -> Item(settings) })
    val FLINT_KNIFE: Item = register(
        "flint_knife",
        { settings: Item.Settings -> KnifeItem(ToolMaterialsMod.FLINT_MATERIAL, settings, 1.0f) })
    val FLINT_AXE: Item = register(
        "flint_axe",
        { settings: Item.Settings -> AxeItem(ToolMaterialsMod.FLINT_MATERIAL, 3.0f, -3.2F, settings) })
    val FIRE_DRILL: Item =
        register("fire_drill", { settings: Item.Settings -> FireDrillItem(settings.maxCount(1).maxDamage(10)) })

    val WOODEN_HARPOON = register("wooden_harpoon", { settings: Item.Settings -> WoodenHarpoonItem(settings.maxCount(1).maxDamage(10)) })

    val RAW_ELDERBERRIES: Item = register("raw_elderberries", { settings ->
        Item(
            settings
                .component(DataComponentTypes.FOOD, FoodComponent.Builder()
                    .nutrition(2)
                    .saturationModifier(0.1f)
                    .build()
                )
                .component(DataComponentTypes.CONSUMABLE, ConsumableComponent.builder()
                    .consumeSeconds(1.6f)
                    .useAction(UseAction.EAT)
                    .sound(SoundEvents.ENTITY_GENERIC_EAT)
                    .consumeParticles(true)
                    .consumeEffect(
                        ApplyEffectsConsumeEffect(StatusEffectInstance(StatusEffects.POISON, 60))
                    )
                    .build()
                )
        )
    })

    val COOKED_ELDERBERRIES: Item = register("cooked_elderberries", { settings ->
        Item(
            settings
                .component(DataComponentTypes.FOOD, FoodComponent.Builder()
                    .nutrition(4)
                    .saturationModifier(0.3f)
                    .build()
                )
                .component(DataComponentTypes.CONSUMABLE, ConsumableComponent.builder()
                    .consumeSeconds(1.6f)
                    .useAction(UseAction.EAT)
                    .sound(SoundEvents.ENTITY_GENERIC_EAT)
                    .consumeParticles(true)
                    .build()
        ))
    })

    val TAB_ICON_ITEM: Item = register("tab_icon", { settings: Item.Settings -> Item(settings.maxCount(1)) })

    val MANTIS_SPAWN_EGG: Item = register(
    "boar_spawn_egg",
    { setting: Item.Settings? -> SpawnEggItem(ModEntities.BOAR_ENTITY, setting) })

    /**
     * Initializes item group registrations and any other post-registration setup.
     * 
     * This method is called after all items are registered to handle secondary
     * initialization tasks like adding items to vanilla item groups.
     */
    fun initialize() {
        // Add bone spear to the vanilla combat item group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
            .register(ModifyEntries { itemGroup: FabricItemGroupEntries -> itemGroup.add(BONE_SPEAR) })
    }

    /**
     * Registers an item with the game registry.
     * 
     * This helper method handles the common pattern of creating a registry key,
     * constructing the item with appropriate settings, and registering it.
     * 
     * @param name The item's resource name (without namespace)
     * @param itemFactory A function that creates the item instance from settings
     * @param settings Optional settings to apply to the item (default: empty settings)
     * @return The registered item instance
     */
    fun register(name: String,
                 itemFactory: (Item.Settings) -> Item,
                 settings: Item.Settings = Item.Settings()): Item {
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name))
        val item: Item = itemFactory(settings.registryKey(itemKey))
        Registry.register(Registries.ITEM, itemKey, item)
        return item
    }

}