package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModTags
import net.minecraft.advancement.*
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.OnKilledCriterion
import net.minecraft.entity.EntityType
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import java.util.function.Consumer

object HuntingAdvancements {

    fun generate(
        parent: AdvancementEntry, consumer: Consumer<AdvancementEntry>, registries: RegistryWrapper.WrapperLookup
    ) {
        // Using the new helper for hunting advancement - demonstrates the improved workflow
        val huntAggressiveMob: AdvancementEntry = AdvancementHelpers.createHuntingAdvancement(
            parent = parent,
            consumer = consumer,
            displayItem = ModItems.WOODEN_SPEAR,
            titleKey = "hunt_aggressive",
            descriptionKey = "hunt_aggressive",
            entityTag = ModTags.Entity.AGGRESSIVE,
            registryLookup = registries,
            frame = AdvancementFrame.CHALLENGE,
            experience = 2,
            pathId = "awakening/hunt_aggressive"
        )

        // Using the helper for item crafting advancement - much cleaner!
        val craftBoneKnife = AdvancementHelpers.createItemAdvancement(
            parent = huntAggressiveMob,
            consumer = consumer,
            displayItem = ModItems.BONE_KNIFE,
            titleKey = "craft_bone_knife",
            frame = AdvancementFrame.TASK,
            experience = 2,
            pathId = "awakening/craft_bone_knife"
        )

        AdvancementHelpers.createItemAdvancement(
            parent = craftBoneKnife,
            consumer = consumer,
            displayItem = ModItems.BONE_SPEAR,
            titleKey = "craft_bone_spear",
            frame = AdvancementFrame.TASK,
            experience = 2,
            pathId = "awakening/craft_bone_spear"
        )

        // Complex advancement with multiple criteria - using builder pattern helper
        AdvancementHelpers.craftingAdvancement(huntAggressiveMob, consumer)
            .item(ModItems.HIDE_TUNIC)
            .titleKey("wear_hide_armor")
            .frame(AdvancementFrame.TASK)
            .experience(4)
            .criterion("wear_hide_tunic", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_TUNIC))
            .criterion("wear_hide_leggings", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_LEGGINGS))
            .criterion("wear_hide_cap", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_CAP))
            .criterion("wear_hide_shoes", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_SHOES))
            .requirementsAllOf(listOf("wear_hide_tunic", "wear_hide_leggings", "wear_hide_cap", "wear_hide_shoes"))
            .build("awakening/wear_hide_armor")
    }
}
