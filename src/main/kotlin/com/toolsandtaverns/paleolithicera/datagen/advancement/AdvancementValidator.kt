package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

/**
 * Advancement validation utilities to ensure comprehensive coverage of mod features.
 * 
 * This object helps developers identify items/features that may be missing advancements,
 * promoting consistent progression design throughout the mod.
 */
object AdvancementValidator {
    
    /**
     * Categories of items that should typically have advancement coverage.
     */
    enum class ItemCategory {
        TOOLS,          // Knives, axes, hammers - always need crafting advancement
        WEAPONS,        // Spears, slings - craft + combat advancements
        ARMOR,          // Hide armor pieces - wear/craft advancements
        CONSUMABLES,    // Food, potions - craft + consume advancements
        CRAFTING_STATIONS, // Workbenches, stations - place + use advancements
        PROGRESSION_ITEMS, // Key items for unlocking new content
        DECORATIVE      // Optional advancement coverage
    }
    
    /**
     * Maps mod items to their expected advancement categories.
     * Add new items here when implementing features to ensure advancement coverage.
     */
    private val itemCategories = mapOf(
        // Tools - should have crafting + usage advancements
        ModItems.BONE_KNIFE to ItemCategory.TOOLS,
        ModItems.FLINT_KNIFE to ItemCategory.TOOLS,
        ModItems.FLINT_AXE to ItemCategory.TOOLS,
        ModItems.FIRE_DRILL to ItemCategory.TOOLS,
        
        // Weapons - should have crafting + kill advancements
        ModItems.WOODEN_SPEAR to ItemCategory.WEAPONS,
        ModItems.BONE_SPEAR to ItemCategory.WEAPONS,
        ModItems.WOODEN_HARPOON to ItemCategory.WEAPONS,
        ModItems.BONE_HARPOON to ItemCategory.WEAPONS,
        ModItems.SLING to ItemCategory.WEAPONS,
        
        // Armor - should have crafting + wearing advancements
        ModItems.HIDE_TUNIC to ItemCategory.ARMOR,
        ModItems.HIDE_LEGGINGS to ItemCategory.ARMOR,
        ModItems.HIDE_CAP to ItemCategory.ARMOR,
        ModItems.HIDE_SHOES to ItemCategory.ARMOR,
        
        // Consumables - should have preparation + consumption advancements
        ModItems.COOKED_ELDERBERRIES to ItemCategory.CONSUMABLES,
        
        // Progression items - core to mod progression
        ModItems.FLINT_BIFACE to ItemCategory.PROGRESSION_ITEMS,
        ModItems.ROCK_CHUNK to ItemCategory.PROGRESSION_ITEMS,
        ModItems.PLANT_FIBER to ItemCategory.PROGRESSION_ITEMS
    )
    
    /**
     * Advancement patterns that should exist for each item category.
     */
    private val expectedAdvancementPatterns = mapOf(
        ItemCategory.TOOLS to listOf("craft_", "use_"),
        ItemCategory.WEAPONS to listOf("craft_", "kill_with_"),
        ItemCategory.ARMOR to listOf("craft_", "wear_"),
        ItemCategory.CONSUMABLES to listOf("craft_", "consume_"),
        ItemCategory.CRAFTING_STATIONS to listOf("place_", "use_"),
        ItemCategory.PROGRESSION_ITEMS to listOf("get_", "craft_")
    )
    
    /**
     * Validates that key items have appropriate advancement coverage.
     * This can be called during development to identify missing advancements.
     * 
     * @return List of validation issues found
     */
    fun validateAdvancementCoverage(): List<String> {
        val issues = mutableListOf<String>()
        
        // Check for items that should have advancements but might not
        itemCategories.forEach { (item, category) ->
            val itemName = getItemName(item)
            val expectedPatterns = expectedAdvancementPatterns[category] ?: emptyList()
            
            expectedPatterns.forEach { pattern ->
                val expectedAdvancementKey = "$pattern$itemName"
                issues.add("Consider adding advancement: $expectedAdvancementKey for ${category.name} item $itemName")
            }
        }
        
        return issues
    }
    
    /**
     * Gets a clean item name for advancement key generation.
     */
    private fun getItemName(item: Item): String {
        val id = Registries.ITEM.getId(item)
        return if (id.namespace == MOD_ID) {
            id.path
        } else {
            "${id.namespace}_${id.path}"
        }
    }
    
    /**
     * Suggests advancement structure for a new item.
     * 
     * @param item The item to generate suggestions for
     * @param category The category this item belongs to
     * @return List of suggested advancement keys and descriptions
     */
    fun suggestAdvancementsForItem(item: Item, category: ItemCategory): List<AdvancementSuggestion> {
        val itemName = getItemName(item)
        val patterns = expectedAdvancementPatterns[category] ?: emptyList()
        
        return patterns.map { pattern ->
            AdvancementSuggestion(
                key = "$pattern$itemName",
                description = generateDescription(pattern, itemName),
                frameType = getFrameType(pattern),
                experienceReward = getExperienceReward(category, pattern)
            )
        }
    }
    
    private fun generateDescription(pattern: String, itemName: String): String {
        return when (pattern) {
            "craft_" -> "Craft a $itemName to aid your survival"
            "use_" -> "Use your $itemName effectively" 
            "kill_with_" -> "Defeat a hostile mob using your $itemName"
            "wear_" -> "Equip your $itemName for protection"
            "consume_" -> "Eat or drink your $itemName"
            "place_" -> "Place a $itemName in the world"
            "get_" -> "Obtain a $itemName"
            else -> "Achievement related to $itemName"
        }
    }
    
    private fun getFrameType(pattern: String): String {
        return when (pattern) {
            "craft_", "place_", "get_", "wear_" -> "TASK"
            "use_", "consume_" -> "TASK" 
            "kill_with_" -> "GOAL"
            else -> "TASK"
        }
    }
    
    private fun getExperienceReward(category: ItemCategory, pattern: String): Int {
        return when (category) {
            ItemCategory.PROGRESSION_ITEMS -> 3
            ItemCategory.WEAPONS -> if (pattern == "kill_with_") 5 else 2
            ItemCategory.TOOLS -> 2
            ItemCategory.ARMOR -> if (pattern == "wear_") 3 else 2
            ItemCategory.CONSUMABLES -> 1
            ItemCategory.CRAFTING_STATIONS -> 3
            ItemCategory.DECORATIVE -> 1
        }
    }
    
    data class AdvancementSuggestion(
        val key: String,
        val description: String, 
        val frameType: String,
        val experienceReward: Int
    )
    
    /**
     * Quick helper to add a new item to tracking.
     * Call this when implementing new features to ensure advancement coverage.
     * 
     * @param item The new item
     * @param category What category of item this is
     */
    fun registerItemForAdvancementTracking(item: Item, category: ItemCategory) {
        // In development, you can use this to print advancement suggestions:
        val suggestions = suggestAdvancementsForItem(item, category)
        println("=== Advancement suggestions for ${getItemName(item)} ===")
        suggestions.forEach { suggestion ->
            println("Key: ${suggestion.key}")
            println("Description: ${suggestion.description}")
            println("Frame: ${suggestion.frameType}, XP: ${suggestion.experienceReward}")
            println("Example helper call:")
            println("AdvancementHelpers.createItemAdvancement(")
            println("    parent = parentAdvancement,")
            println("    consumer = consumer,")
            println("    displayItem = item,")
            println("    titleKey = \"${suggestion.key}\",")
            println("    frame = AdvancementFrame.${suggestion.frameType},")
            println("    experience = ${suggestion.experienceReward},")
            println("    pathId = \"awakening/${suggestion.key}\"")
            println(")")
            println()
        }
    }
}