package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.item.Items
import net.minecraft.text.Text
import java.util.function.Consumer

object GatherStickAdvancements {

    fun generate(parent: AdvancementEntry, consumer: Consumer<AdvancementEntry>) {
        // Get Stick
        val getStick: AdvancementEntry = Advancement.Builder.create()
            .parent(parent)
            .display(
                Items.STICK,
                Text.translatable("advancement.$MOD_ID.awakening.get_stick.title"),
                Text.translatable("advancement.$MOD_ID.awakening.get_stick.description"),
                null,
                AdvancementFrame.TASK,
                true, false, false
            )
            .criterion("get_stick", InventoryChangedCriterion.Conditions.items(Items.STICK))
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/get_stick")

        Advancement.Builder.create()
            .parent(getStick)
            .display(
                ModItems.WOODEN_SPEAR,
                Text.translatable("advancement.$MOD_ID.awakening.craft_wooden_spear.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_wooden_spear.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion("craft_wooden_spear", InventoryChangedCriterion.Conditions.items(ModItems.WOODEN_SPEAR))
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/craft_wooden_spear")

        Advancement.Builder.create()
            .parent(getStick)
            .display(
                ModItems.WOODEN_HARPOON,
                Text.translatable("advancement.$MOD_ID.awakening.craft_wooden_harpoon.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_wooden_harpoon.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion("craft_wooden_harpoon", InventoryChangedCriterion.Conditions.items(ModItems.WOODEN_HARPOON))
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "craft_wooden_harpoon")

        // Food Dryer placement
        val placeFoodDryer: AdvancementEntry = Advancement.Builder.create()
            .parent(getStick)
            .display(
                ModBlocks.FOOD_DRYER,
                Text.translatable("advancement.$MOD_ID.awakening.place_food_dryer.title"),
                Text.translatable("advancement.$MOD_ID.awakening.place_food_dryer.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "placed_food_dryer",
                InventoryChangedCriterion.Conditions.items(ModBlocks.FOOD_DRYER)
            )
            .rewards(AdvancementRewards.Builder.experience(3))
            .build(consumer, "awakening/place_food_dryer")

        // Challenge: Collect all dried foods
        Advancement.Builder.create()
            .parent(placeFoodDryer)
            .display(
                ModItems.DRIED_BEEF,
                Text.translatable("advancement.$MOD_ID.awakening.collect_all_dried_foods.title"),
                Text.translatable("advancement.$MOD_ID.awakening.collect_all_dried_foods.description"),
                null,
                AdvancementFrame.CHALLENGE,
                true, true, false
            )
            .criterion("dried_beef", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_BEEF))
            .criterion("dried_pork", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_PORK))
            .criterion("dried_chicken", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_CHICKEN))
            .criterion("dried_mutton", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_MUTTON))
            .criterion("dried_rabbit", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_RABBIT))
            .criterion("dried_cod", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_COD))
            .criterion("dried_salmon", InventoryChangedCriterion.Conditions.items(ModItems.DRIED_SALMON))
            .requirements(
                AdvancementRequirements.allOf(
                    listOf(
                        "dried_beef",
                        "dried_pork",
                        "dried_chicken",
                        "dried_mutton",
                        "dried_rabbit",
                        "dried_cod",
                        "dried_salmon"
                    )
                )
            )
            .rewards(AdvancementRewards.Builder.experience(10))
            .build(consumer, "awakening/collect_all_dried_foods")
    }

}