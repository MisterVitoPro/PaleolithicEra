package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.text.Text
import java.util.function.Consumer

object ElderberryAdvancements {

    fun generate(parent: AdvancementEntry, consumer: Consumer<AdvancementEntry>) {

        val harvestBerries: AdvancementEntry = Advancement.Builder.create()
            .parent(parent)
            .display(
                ModItems.EDIBLE_PLANTS[EdiblePlants.ELDERBERRY],
                Text.translatable("advancement.$MOD_ID.awakening.harvest_berries.title"),
                Text.translatable("advancement.$MOD_ID.awakening.harvest_berries.description"),
                null,
                AdvancementFrame.CHALLENGE,
                true, true, false
            )
            .criterion("has_raw_elderberries",InventoryChangedCriterion.Conditions.items(ModItems.EDIBLE_PLANTS[EdiblePlants.ELDERBERRY]))
            .criterion("has_yarrow_herb", InventoryChangedCriterion.Conditions.items(ModItems.EDIBLE_PLANTS[EdiblePlants.YARROW]))
            .criterion("has_chamomile_herb", InventoryChangedCriterion.Conditions.items(ModItems.EDIBLE_PLANTS[EdiblePlants.CHAMOMILE]))
            .rewards(AdvancementRewards.Builder.experience(9))
            .build(consumer, "awakening/harvest_berries")

        Advancement.Builder.create()
            .parent(harvestBerries)
            .display(
                ModItems.COOKED_ELDERBERRIES,
                Text.translatable("advancement.$MOD_ID.awakening.eat_berries.title"),
                Text.translatable("advancement.$MOD_ID.awakening.eat_berries.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion("eaten_berries", InventoryChangedCriterion.Conditions.items(ModItems.COOKED_ELDERBERRIES))
            .rewards(AdvancementRewards.Builder.experience(3))
            .build(consumer, "awakening/eat_berries")
    }
}
