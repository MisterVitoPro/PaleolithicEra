package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import net.minecraft.advancement.*
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.OnKilledCriterion
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

/**
 * Helper utilities for creating advancements in the Paleolithic Era mod.
 * 
 * These utilities reduce boilerplate code and ensure consistency across all advancement definitions.
 * They follow Fabric's best practices for data generation and make it easier to add new advancements
 * when developing new features.
 */
object AdvancementHelpers {

    /**
     * Builder pattern for creating simple item crafting/obtaining advancements.
     * 
     * Example usage:
     * ```
     * val craftSling = craftingAdvancement(parent, consumer)
     *     .item(ModItems.SLING)
     *     .titleKey("craft_sling")
     *     .frame(AdvancementFrame.TASK)
     *     .experience(2)
     *     .build("awakening/craft_sling")
     * ```
     */
    fun craftingAdvancement(
        parent: AdvancementEntry,
        consumer: Consumer<AdvancementEntry>
    ): AdvancementBuilderHelper {
        return AdvancementBuilderHelper(parent, consumer)
    }

    /**
     * Creates a hunting advancement that triggers when killing entities with a specific tag.
     * 
     * @param parent Parent advancement
     * @param consumer Advancement consumer
     * @param displayItem Item to display as the advancement icon
     * @param titleKey Translation key for the title (without mod prefix)
     * @param descriptionKey Translation key for the description (without mod prefix)
     * @param entityTag Tag containing entities that must be killed
     * @param registryLookup Registry lookup for entity types
     * @param frame Advancement frame type
     * @param experience Experience reward
     * @param pathId Path for the advancement ID
     * @return The created advancement entry
     */
    fun createHuntingAdvancement(
        parent: AdvancementEntry,
        consumer: Consumer<AdvancementEntry>,
        displayItem: ItemConvertible,
        titleKey: String,
        descriptionKey: String,
        entityTag: TagKey<EntityType<*>>,
        registryLookup: RegistryWrapper.WrapperLookup,
        frame: AdvancementFrame = AdvancementFrame.TASK,
        experience: Int = 2,
        pathId: String
    ): AdvancementEntry {
        return Advancement.Builder.create()
            .parent(parent)
            .display(
                displayItem,
                Text.translatable("advancement.$MOD_ID.awakening.$titleKey.title"),
                Text.translatable("advancement.$MOD_ID.awakening.$titleKey.description"),
                null,
                frame,
                true, true, false
            )
            .criterion(
                "kill_entity",
                OnKilledCriterion.Conditions.createPlayerKilledEntity(
                    EntityPredicate.Builder.create()
                        .type(registryLookup.getOrThrow(net.minecraft.registry.RegistryKeys.ENTITY_TYPE), entityTag)
                )
            )
            .rewards(AdvancementRewards.Builder.experience(experience))
            .build(consumer, pathId)
    }

    /**
     * Creates a simple item obtaining advancement with standard settings.
     * 
     * @param parent Parent advancement
     * @param consumer Advancement consumer
     * @param displayItem Item to display and require for completion
     * @param titleKey Translation key for title (without mod prefix)
     * @param frame Advancement frame type
     * @param showToast Whether to show toast notification
     * @param announceToChat Whether to announce in chat
     * @param hidden Whether advancement is hidden
     * @param experience Experience reward
     * @param pathId Path for the advancement ID
     * @return The created advancement entry
     */
    fun createItemAdvancement(
        parent: AdvancementEntry,
        consumer: Consumer<AdvancementEntry>,
        displayItem: ItemConvertible,
        titleKey: String,
        frame: AdvancementFrame = AdvancementFrame.TASK,
        showToast: Boolean = true,
        announceToChat: Boolean = true,
        hidden: Boolean = false,
        experience: Int = 2,
        pathId: String
    ): AdvancementEntry {
        return Advancement.Builder.create()
            .parent(parent)
            .display(
                displayItem,
                Text.translatable("advancement.$MOD_ID.awakening.$titleKey.title"),
                Text.translatable("advancement.$MOD_ID.awakening.$titleKey.description"),
                null,
                frame,
                showToast, announceToChat, hidden
            )
            .criterion(
                "has_${titleKey}",
                InventoryChangedCriterion.Conditions.items(displayItem)
            )
            .rewards(AdvancementRewards.Builder.experience(experience))
            .build(consumer, pathId)
    }

    /**
     * Helper class for building advancements with a fluent API.
     */
    class AdvancementBuilderHelper(
        private val parent: AdvancementEntry,
        private val consumer: Consumer<AdvancementEntry>
    ) {
        private var item: ItemConvertible? = null
        private var titleKey: String? = null
        private var descriptionKey: String? = null
        private var frame: AdvancementFrame = AdvancementFrame.TASK
        private var showToast: Boolean = true
        private var announceToChat: Boolean = true
        private var hidden: Boolean = false
        private var experience: Int = 2
        private var background: Identifier? = null
        private val criteria = mutableMapOf<String, AdvancementCriterion<*>>()
        private var requirements: AdvancementRequirements? = null

        fun item(item: ItemConvertible): AdvancementBuilderHelper {
            this.item = item
            return this
        }

        fun titleKey(key: String): AdvancementBuilderHelper {
            this.titleKey = key
            this.descriptionKey = key // Default description key to same as title
            return this
        }

        fun titleAndDescriptionKey(titleKey: String, descriptionKey: String): AdvancementBuilderHelper {
            this.titleKey = titleKey
            this.descriptionKey = descriptionKey
            return this
        }

        fun frame(frame: AdvancementFrame): AdvancementBuilderHelper {
            this.frame = frame
            return this
        }

        fun display(showToast: Boolean, announceToChat: Boolean, hidden: Boolean): AdvancementBuilderHelper {
            this.showToast = showToast
            this.announceToChat = announceToChat
            this.hidden = hidden
            return this
        }

        fun experience(experience: Int): AdvancementBuilderHelper {
            this.experience = experience
            return this
        }

        fun background(background: Identifier): AdvancementBuilderHelper {
            this.background = background
            return this
        }

        fun criterion(name: String, criterion: AdvancementCriterion<*>): AdvancementBuilderHelper {
            criteria[name] = criterion
            return this
        }

        fun requirementsAllOf(criteriaNames: List<String>): AdvancementBuilderHelper {
            this.requirements = AdvancementRequirements.allOf(criteriaNames)
            return this
        }

        fun requirementsAnyOf(criteriaNames: List<String>): AdvancementBuilderHelper {
            this.requirements = AdvancementRequirements.anyOf(criteriaNames)
            return this
        }

        fun build(pathId: String): AdvancementEntry {
            require(item != null) { "Item must be specified for advancement" }
            require(titleKey != null) { "Title key must be specified for advancement" }

            val builder = Advancement.Builder.create()
                .parent(parent)
                .display(
                    item!!,
                    Text.translatable("advancement.$MOD_ID.awakening.$titleKey.title"),
                    Text.translatable("advancement.$MOD_ID.awakening.$descriptionKey.description"),
                    background,
                    frame,
                    showToast, announceToChat, hidden
                )
                .rewards(AdvancementRewards.Builder.experience(experience))

            // Add criteria - if none specified, default to item inventory criterion
            if (criteria.isEmpty()) {
                builder.criterion("has_$titleKey", InventoryChangedCriterion.Conditions.items(item!!))
            } else {
                criteria.forEach { (name, criterion) -> builder.criterion(name, criterion) }
            }

            // Add requirements if specified
            requirements?.let { builder.requirements(it) }

            return builder.build(consumer, pathId)
        }
    }
}