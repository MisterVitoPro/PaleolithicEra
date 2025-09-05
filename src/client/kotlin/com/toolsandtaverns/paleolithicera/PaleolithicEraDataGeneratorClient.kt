package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.datagen.*
import com.toolsandtaverns.paleolithicera.datagen.loot.EntityLootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.loot.ModBlockLootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.EdiblePlantRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.FoodDryingRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.KnappingRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.VanillaRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.tag.ModBlockTagProvider
import com.toolsandtaverns.paleolithicera.datagen.tag.ModEntityTypeTagProvider
import com.toolsandtaverns.paleolithicera.datagen.tag.ModItemTagProvider
import com.toolsandtaverns.paleolithicera.world.ModConfiguredFeatures
import com.toolsandtaverns.paleolithicera.world.ModPlacedFeatures
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys

/**
 * Data generator entry point for the Paleolithic Era mod.
 *
 * This class is responsible for setting up all the data generators that create assets
 * during the build process, such as recipes, loot tables, models, and tags.
 */
object PaleolithicEraDataGeneratorClient : DataGeneratorEntrypoint {

    /**
     * Initializes all data generators for the mod.
     *
     * This method sets up data generators for creating JSON files for various game components
     * including recipes, loot tables, models, block/item tags, and advancements.
     *
     * Providers are ordered by dependency:
     * 1. Tags (needed by recipes and loot tables)
     * 2. Registry data (needed by other providers)
     * 3. Recipes and loot tables (core gameplay data)
     * 4. Models (client-side assets)
     * 5. Advancements (depends on items/blocks existing)
     *
     * @param fabricDataGenerator The data generator provided by Fabric
     */
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()
        
        // PHASE 1: Tag Providers (other providers depend on these)
        pack.addProvider(::ModBlockTagProvider)
        pack.addProvider(::ModItemTagProvider)  
        pack.addProvider(::ModEntityTypeTagProvider)
        
        // PHASE 2: Registry Data Provider (defines dynamic registry content)
        pack.addProvider(::ModDynamicRegistryProvider)
        
        // PHASE 3: Core Game Data Providers (recipes, loot tables)
        pack.addProvider(::VanillaRecipeProvider)
        pack.addProvider(::KnappingRecipeProvider)
        pack.addProvider(::EdiblePlantRecipeProvider)
        pack.addProvider(::FoodDryingRecipeProvider)
        pack.addProvider(::ModBlockLootTableProvider)
        pack.addProvider(::EntityLootTableProvider)
        
        // PHASE 4: Client Assets Provider
        pack.addProvider(::ModModelProvider)
        
        // PHASE 5: Advancement Provider (depends on all items/blocks/recipes existing)
        // This ensures advancements can reference all mod content
        pack.addProvider(::ModAdvancementProvider)
    }

    /**
     * Builds custom registry entries for the mod.
     *
     * This method registers world generation features that need to be available
     * during the data generation process, including configured features and
     * placed features for structures, ores, and vegetation.
     *
     * @param registryBuilder The registry builder to add registry entries to
     */
    override fun buildRegistry(registryBuilder: RegistryBuilder) {
        // Register configured features (basic feature definitions)
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
        // Register placed features (feature placement in the world)
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
    }

}