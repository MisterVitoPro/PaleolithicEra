package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModCriteria
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.data.advancement.AdvancementTabGenerator
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

object PaleolithicEraAdvancementTab : AdvancementTabGenerator {
    override fun accept(
        registries: RegistryWrapper.WrapperLookup,
        consumer: Consumer<AdvancementEntry>
    ) {
        // Stage 0 Root
        val tabRoot: AdvancementEntry = Advancement.Builder.create()
            .display(
                ModItems.TAB_ICON_ITEM,
                Text.translatable("advancement.$MOD_ID.awakening.title"),
                Text.translatable("advancement.$MOD_ID.awakening.description"),
                Identifier.ofVanilla("gui/advancements/backgrounds/stone"),
                AdvancementFrame.TASK,
                false,
                false,
                true
            )
            .criterion("tick", TickCriterion.Conditions.createTick())
            .build(consumer, "awakening/tab_root")

        ElderberryAdvancements.generate(tabRoot, consumer)
        GatherStickAdvancements.generate(tabRoot, consumer)
        HuntingAdvancements.generate(tabRoot, consumer, registries)

        Advancement.Builder.create()
            .parent(tabRoot)
            .display(
                ModBlocks.CRUDE_BED,
                Text.translatable("advancement.$MOD_ID.awakening.crude_bed.title"),
                Text.translatable("advancement.$MOD_ID.awakening.crude_bed.desc"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "place_crude_bed",
                InventoryChangedCriterion.Conditions.items(ModBlocks.CRUDE_BED)
            )
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/place_crude_bed")

        val getRockChunk = Advancement.Builder.create()
            .parent(tabRoot)
            .display(
                ModItems.ROCK_CHUNK,
                Text.translatable("advancement.$MOD_ID.awakening.get_rock_chunk.title"),
                Text.translatable("advancement.$MOD_ID.awakening.get_rock_chunk.description"),
                null,
                AdvancementFrame.TASK,
                true,
                false,
                false
            )
            .criterion(
                "has_rock_chunk",
                InventoryChangedCriterion.Conditions.items(ModItems.ROCK_CHUNK)
            )
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/has_rock_chunk")

        val craftKnapping = Advancement.Builder.create()
            .parent(getRockChunk)
            .display(
                ModBlocks.KNAPPING_STATION,
                Text.translatable("advancement.$MOD_ID.awakening.craft_knapping_station.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_knapping_station.description"),
                null,
                AdvancementFrame.TASK,
                true,
                false,
                false
            )
            .criterion(
                "craft_knap_station",
                InventoryChangedCriterion.Conditions.items(ModBlocks.KNAPPING_STATION)
            )
            .rewards(AdvancementRewards.Builder.experience(3))
            .build(consumer, "awakening/craft_knap_station")

        val craftFlintBiface: AdvancementEntry = Advancement.Builder.create()
            .parent(craftKnapping)
            .display(
                ModItems.FLINT_BIFACE,
                Text.translatable("advancement.$MOD_ID.awakening.craft_flint_biface.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_flint_biface.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            )
            .criterion(
                "craft_flint_biface",
                InventoryChangedCriterion.Conditions.items(ModItems.FLINT_BIFACE)
            )
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/craft_flint_biface")

        // Gather Plant Fiber
        val gatherPlantFiber: AdvancementEntry = Advancement.Builder.create()
            .parent(craftFlintBiface)
            .display(
                Registries.ITEM.get(Identifier.of(MOD_ID, "plant_fiber")),
                Text.translatable("advancement.$MOD_ID.awakening.gather_plant_fiber.title"),
                Text.translatable("advancement.$MOD_ID.awakening.gather_plant_fiber.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "gather_plant_fiber", InventoryChangedCriterion.Conditions.items(ModItems.PLANT_FIBER)
            )
            .rewards(AdvancementRewards.Builder.experience(2))
            .build(consumer, "awakening/gather_plant_fiber")

        val placeFirePit: AdvancementEntry = Advancement.Builder.create()
            .parent(gatherPlantFiber)
            .display(
                ModBlocks.CRUDE_CAMPFIRE,
                Text.translatable("advancement.$MOD_ID.awakening.place_crude_campfire.title"),
                Text.translatable("advancement.$MOD_ID.awakening.place_crude_campfire.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "placed_crude_campfire",
                InventoryChangedCriterion.Conditions.items(ModBlocks.CRUDE_CAMPFIRE)
            )
            .rewards(AdvancementRewards.Builder.experience(5))
            .build(consumer, "awakening/place_crude_campfire")

        val lightCrudeCampfire: AdvancementEntry = Advancement.Builder.create()
            .parent(placeFirePit)
            .display(
                ModItems.FIRE_DRILL,
                Text.translatable("advancement.$MOD_ID.awakening.light_crude_campfire.title"),
                Text.translatable("advancement.$MOD_ID.awakening.light_crude_campfire.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "lit_crude_campfire",
                ModCriteria.LIT_CRUDE_CAMPFIRE.create()
            )
            .rewards(AdvancementRewards.Builder.experience(3))
            .build(consumer, "awakening/light_crude_campfire")

        Advancement.Builder.create()
            .parent(lightCrudeCampfire)
            .display(
                Items.COOKED_BEEF,
                Text.translatable("advancement.$MOD_ID.awakening.cook_meat.title"),
                Text.translatable("advancement.$MOD_ID.awakening.cook_meat.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion("cooked_beef", InventoryChangedCriterion.Conditions.items(Items.COOKED_BEEF))
            .criterion("cooked_chicken", InventoryChangedCriterion.Conditions.items(Items.COOKED_CHICKEN))
            .criterion("cooked_mutton", InventoryChangedCriterion.Conditions.items(Items.COOKED_MUTTON))
            .criterion("cooked_rabbit", InventoryChangedCriterion.Conditions.items(Items.COOKED_RABBIT))
            .criterion("cooked_porkchop", InventoryChangedCriterion.Conditions.items(Items.COOKED_PORKCHOP))
            .criterion("cooked_cod", InventoryChangedCriterion.Conditions.items(Items.COOKED_COD))
            .criterion("cooked_salmon", InventoryChangedCriterion.Conditions.items(Items.COOKED_SALMON))
            .requirements(AdvancementRequirements.anyOf(
                listOf(
                    "cooked_beef",
                    "cooked_chicken",
                    "cooked_mutton",
                    "cooked_rabbit",
                    "cooked_porkchop",
                    "cooked_cod",
                    "cooked_salmon"
                )))
            .rewards(AdvancementRewards.Builder.experience(3))
            .build(consumer, "awakening/cook_meat")

    }

}
