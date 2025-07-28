package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModCriteria
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
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

        // Get Stick
        val getStick: AdvancementEntry = Advancement.Builder.create()
            .parent(tabRoot)
            .display(
                Items.STICK,
                Text.translatable("advancement.$MOD_ID.awakening.get_stick.title"),
                Text.translatable("advancement.$MOD_ID.awakening.get_stick.description"),
                null,
                AdvancementFrame.TASK,
                true, false, false
            )
            .criterion("get_stick", InventoryChangedCriterion.Conditions.items(Items.STICK))
            .build(consumer, "awakening/get_stick")

        val getRockChunk = Advancement.Builder.create()
            .parent(tabRoot)
            .display(
                ModItems.ROCK_CHUNK, // Replace with your actual item reference
                Text.translatable("advancement.$MOD_ID.awakening.get_rock_chunk.title"),
                Text.translatable("advancement.$MOD_ID.awakening.get_rock_chunk.description"),
                null,
                AdvancementFrame.TASK,
                true, // show toast
                false, // announce to chat
                false // not hidden
            )
            .criterion(
                "has_rock_chunk",
                InventoryChangedCriterion.Conditions.items(ModItems.ROCK_CHUNK)
            )
            .build(consumer, "awakening/has_rock_chunk")

        val craftKnapping = Advancement.Builder.create()
            .parent(getRockChunk)
            .parent(getStick)
            .display(
                ModBlocks.KNAPPING_STATION, // Replace with your actual item reference
                Text.translatable("advancement.$MOD_ID.awakening.craft_knapping_station.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_knapping_station.description"),
                null,
                AdvancementFrame.TASK,
                true, // show toast
                false, // announce to chat
                false // not hidden
            )
            .criterion(
                "craft_knap_station",
                InventoryChangedCriterion.Conditions.items(ModBlocks.KNAPPING_STATION)
            )
            .build(consumer, "awakening/craft_knap_station")

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
            .build(consumer, "awakening/craft_wooden_spear")

        // Get Bone
        val getBone: AdvancementEntry = Advancement.Builder.create()
            .parent(getStick)
            .display(
                Items.BONE,
                Text.translatable("advancement.$MOD_ID.awakening.get_bone.title"),
                Text.translatable("advancement.$MOD_ID.awakening.get_bone.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion("get_bone", InventoryChangedCriterion.Conditions.items(Items.BONE))
            .build(consumer, "awakening/get_bone")

        // Craft Bone Knife
        val craftBoneKnife: AdvancementEntry = Advancement.Builder.create()
            .parent(getBone)
            .display(
                Registries.ITEM.get(Identifier.of(MOD_ID, "bone_knife")),
                Text.translatable("advancement.$MOD_ID.awakening.craft_bone_knife.title"),
                Text.translatable("advancement.$MOD_ID.awakening.craft_bone_knife.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "craft_bone_knife", InventoryChangedCriterion.Conditions.items(ModItems.BONE_KNIFE)
            )
            .build(consumer, "awakening/craft_bone_knife")

        // Gather Plant Fiber
        val gatherPlantFiber: AdvancementEntry = Advancement.Builder.create()
            .parent(craftBoneKnife)
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
            .build(consumer, "awakening/gather_plant_fiber")

        val placeFirePit: AdvancementEntry = Advancement.Builder.create()
            .parent(gatherPlantFiber)
            .display(
                ModBlocks.CRUDE_CAMPFIRE, // Replace with your custom Fire Pit item
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
            .build(consumer, "awakening/place_crude_campfire")

        val lightCrudeCampfire: AdvancementEntry = Advancement.Builder.create()
            .parent(placeFirePit)
            .display(
                ModItems.FIRE_DRILL, // Replace with your Fire Drill if needed
                Text.translatable("advancement.$MOD_ID.awakening.light_crude_campfire.title"),
                Text.translatable("advancement.$MOD_ID.awakening.light_crude_campfire.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "lit_crude_campfire",
                ModCriteria.LIT_CRUDE_CAMPFIRE.create()
            ).build(consumer, "awakening/light_crude_campfire")


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
            .criterion(
                "cooked_meat",
                InventoryChangedCriterion.Conditions.items(Items.COOKED_BEEF, Items.COOKED_CHICKEN)
            )
            .build(consumer, "awakening/cook_meat")

        Advancement.Builder.create()
            .parent(lightCrudeCampfire)
            .display(
                ModBlocks.KNAPPING_STATION, // Placeholder for Knapping Station
                Text.translatable("advancement.$MOD_ID.awakening.unlock_knapping.title"),
                Text.translatable("advancement.$MOD_ID.awakening.unlock_knapping.description"),
                null,
                AdvancementFrame.TASK,
                true, true, false
            )
            .criterion(
                "has_knapping_station",
                InventoryChangedCriterion.Conditions.items(ModBlocks.KNAPPING_STATION)
            )
            .build(consumer, "awakening/unlock_knapping_station")


    }

}
