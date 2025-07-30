package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
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

        val craftWoodenSpear: AdvancementEntry = Advancement.Builder.create()
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
    }

}