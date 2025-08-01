
package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModEntityTags.HUNTABLE_TAG
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.OnKilledCriterion
import net.minecraft.entity.EntityType
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import java.util.function.Consumer

object HuntingAdvancements {

    fun generate(parent: AdvancementEntry, consumer: Consumer<AdvancementEntry>, registries: RegistryWrapper.WrapperLookup
    ) {
        val entityTypeRegistry: RegistryWrapper.Impl<EntityType<*>> = registries.getOrThrow(RegistryKeys.ENTITY_TYPE)

        val huntAggressiveMob: AdvancementEntry = Advancement.Builder.create()
            .parent(parent)
            .display(
                ModItems.WOODEN_SPEAR, // You can change this to another relevant item
                Text.translatable("advancement.$MOD_ID.awakening.hunt_aggressive.title"),
                Text.translatable("advancement.$MOD_ID.awakening.hunt_aggressive.description"),
                null,
                AdvancementFrame.CHALLENGE,
                true, true, false
            )
            .criterion(
                "hunt_animal",
                OnKilledCriterion.Conditions.createPlayerKilledEntity(
                    EntityPredicate.Builder.create()
                        .type(entityTypeRegistry, HUNTABLE_TAG)
                )
            )
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/hunt_aggressive")

        val craftBoneKnife: AdvancementEntry = Advancement.Builder.create()
            .parent(huntAggressiveMob)
            .display(
                ModItems.BONE_KNIFE,
                Text.translatable("advancement.$MOD_ID.awakening.craft_bone_knife.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_bone_knife.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "craft_bone_knife", InventoryChangedCriterion.Conditions.items(ModItems.BONE_KNIFE)
            )
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/craft_bone_knife")


        val fullRawHideArmor: AdvancementEntry = Advancement.Builder.create()
            .parent(huntAggressiveMob)
            .display(
               ModItems.HIDE_TUNIC,
                Text.translatable("advancement.$MOD_ID.awakening.wear_hide_armor.title"),
                Text.translatable("advancement.$MOD_ID.awakening.wear_hide_armor.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            )
            .criterion("wear_hide_tunic", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_TUNIC))
            .criterion("wear_hide_leggings", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_LEGGINGS))
            .criterion("wear_hide_cap", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_CAP))
            .criterion("wear_hide_shoes", InventoryChangedCriterion.Conditions.items(ModItems.HIDE_SHOES))
            .requirements(AdvancementRequirements.allOf(listOf("wear_hide_tunic", "wear_hide_leggings", "wear_hide_cap", "wear_hide_shoes")))
            .rewards(AdvancementRewards.Builder.experience(4))
            .build(consumer, "$MOD_ID/wear_hide_armor")
    }
}
