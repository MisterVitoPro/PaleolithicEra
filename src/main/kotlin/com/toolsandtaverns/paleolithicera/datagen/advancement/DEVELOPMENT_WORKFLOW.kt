package com.toolsandtaverns.paleolithicera.datagen.advancement

/**
 * DEVELOPMENT WORKFLOW TEMPLATE FOR PALEOLITHIC ERA MOD
 * =====================================================
 * 
 * This template demonstrates the proper workflow for adding new features to the mod
 * while ensuring advancements are always included and properly integrated.
 * 
 * Follow this pattern when implementing new items, blocks, or gameplay mechanics.
 */

/*
 * STEP 1: DESIGN THE FEATURE
 * ==========================
 * Before implementing any code, consider:
 * 
 * 1. What advancement(s) should this feature unlock?
 * 2. How does this fit into the existing progression tree?
 * 3. What parent advancement should this build upon?
 * 4. What experience reward is appropriate?
 * 5. What frame type (TASK, GOAL, CHALLENGE)?
 * 
 * Example: Adding a new tool "Stone Hammer"
 * - Parent: craft_knapping_station (player has basic stone working)
 * - Advancement: "craft_stone_hammer" (TASK, 3 XP)
 * - Follow-up: Could unlock "break_hard_blocks" advancement
 */

/*
 * STEP 2: IMPLEMENT THE CORE FEATURE
 * ===================================
 * Add your item/block/entity to the appropriate registry:
 * - Items: ModItems.kt
 * - Blocks: ModBlocks.kt  
 * - Entities: ModEntityType.kt
 * 
 * Create the necessary classes in their proper packages.
 * Add textures and models using the data generation system.
 */

/*
 * STEP 3: ADD THE ADVANCEMENT
 * ===========================
 * ALWAYS add advancements for new features. Choose the appropriate method:
 */

// For simple item crafting/obtaining:
fun addSimpleItemAdvancement() {
    // Example: Stone Hammer advancement
    /*
    val craftStoneHammer = AdvancementHelpers.createItemAdvancement(
        parent = parentAdvancement,
        consumer = consumer,
        displayItem = ModItems.STONE_HAMMER,
        titleKey = "craft_stone_hammer",
        frame = AdvancementFrame.TASK,
        experience = 3,
        pathId = "awakening/craft_stone_hammer"
    )
    */
}

// For combat/hunting related advancements:
fun addHuntingAdvancement() {
    // Example: Kill hostile with new weapon
    /*
    val killWithStoneHammer = AdvancementHelpers.createHuntingAdvancement(
        parent = craftStoneHammer,
        consumer = consumer,
        displayItem = ModItems.STONE_HAMMER,
        titleKey = "kill_with_stone_hammer",
        descriptionKey = "kill_with_stone_hammer", 
        entityTag = ModTags.Entity.AGGRESSIVE,
        registryLookup = registries,
        frame = AdvancementFrame.GOAL,
        experience = 5,
        pathId = "awakening/kill_with_stone_hammer"
    )
    */
}

// For complex advancements with multiple criteria:
fun addComplexAdvancement() {
    // Example: Master craftsman (craft multiple stone tools)
    /*
    AdvancementHelpers.craftingAdvancement(parentAdvancement, consumer)
        .item(ModItems.STONE_HAMMER) // Display item
        .titleKey("master_stone_crafting")
        .frame(AdvancementFrame.CHALLENGE)
        .experience(10)
        .criterion("craft_stone_hammer", InventoryChangedCriterion.Conditions.items(ModItems.STONE_HAMMER))
        .criterion("craft_stone_axe", InventoryChangedCriterion.Conditions.items(ModItems.STONE_AXE))
        .criterion("craft_stone_knife", InventoryChangedCriterion.Conditions.items(ModItems.STONE_KNIFE))
        .requirementsAllOf(listOf("craft_stone_hammer", "craft_stone_axe", "craft_stone_knife"))
        .build("awakening/master_stone_crafting")
    */
}

/*
 * STEP 4: ADD TRANSLATION KEYS
 * =============================
 * Add entries to src/main/resources/assets/paleolithic-era/lang/en_us.json:
 * 
 * {
 *   "advancement.paleolithic-era.awakening.craft_stone_hammer.title": "Stone Age Engineer",
 *   "advancement.paleolithic-era.awakening.craft_stone_hammer.description": "Craft a stone hammer for heavy-duty work.",
 *   "advancement.paleolithic-era.awakening.kill_with_stone_hammer.title": "Hammer Time", 
 *   "advancement.paleolithic-era.awakening.kill_with_stone_hammer.description": "Defeat a hostile mob using your stone hammer.",
 *   "advancement.paleolithic-era.awakening.master_stone_crafting.title": "Stone Age Master",
 *   "advancement.paleolithic-era.awakening.master_stone_crafting.description": "Craft all the essential stone tools."
 * }
 */

/*
 * STEP 5: ADD TO ADVANCEMENT TAB
 * ===============================
 * Add your advancement generation to the appropriate advancement module:
 * - Hunting/Combat: HuntingAdvancements.kt
 * - Tools/Crafting: Create a new module like "CraftingAdvancements.kt"
 * - Plants/Food: HerbsAdvancements.kt
 * - General/Misc: PaleolithicEraAdvancementTab.kt (main tab)
 * 
 * Then register the module in PaleolithicEraAdvancementTab.accept():
 */

/*
 * STEP 6: ADD RECIPES AND DATA GENERATION
 * ========================================
 * Add crafting recipes to the appropriate recipe provider:
 * - Vanilla recipes: VanillaRecipeProvider.kt
 * - Knapping recipes: KnappingRecipeProvider.kt
 * - Cooking recipes: EdiblePlantRecipeProvider.kt
 * 
 * Add loot tables if needed:
 * - Block loot: ModBlockLootTableProvider.kt
 * - Entity loot: EntityLootTableProvider.kt
 */

/*
 * STEP 7: TEST EVERYTHING
 * =======================
 * ALWAYS test your implementation:
 * 1. Run data generation: ./gradlew runDatagen
 * 2. Run client: ./gradlew runClient
 * 3. Test in-game:
 *    - Craft/obtain the item
 *    - Verify advancement triggers
 *    - Check advancement tree display
 *    - Test any combat/usage mechanics
 * 4. Verify no console errors
 */

/*
 * STEP 8: COMMIT WITH PROPER MESSAGE
 * ==================================
 * Use descriptive commit messages that mention advancement integration:
 * 
 * Good: "add stone hammer tool with crafting advancement and kill goal"
 * Bad: "add stone hammer"
 * 
 * This helps track that advancements were properly included.
 */

/*
 * BEST PRACTICES CHECKLIST
 * =========================
 * ✓ Feature fits logically into progression tree
 * ✓ Advancement has appropriate parent (not orphaned)
 * ✓ Translation keys added for title and description
 * ✓ Experience reward is balanced (1-5 for basic, 5-15 for complex)
 * ✓ Frame type is appropriate (TASK for basic, GOAL for achievements, CHALLENGE for difficult)
 * ✓ Used helper utilities to reduce boilerplate
 * ✓ Tested data generation and client startup
 * ✓ Verified advancement triggers in-game
 * 
 * FABRIC PROVIDER BEST PRACTICES
 * ===============================
 * ✓ Always extend Fabric provider classes (FabricRecipeProvider, etc.)
 * ✓ Use RegistryWrapper.WrapperLookup for registry access
 * ✓ Implement proper CompletableFuture handling for async registry operations
 * ✓ Use FabricDataOutput instead of raw DataOutput
 * ✓ Register all providers in PaleolithicEraDataGeneratorClient
 * ✓ Keep provider implementations focused (one responsibility per provider)
 */

// This file serves as documentation and should not contain actual implementation code.
// Copy the patterns above when implementing real features.