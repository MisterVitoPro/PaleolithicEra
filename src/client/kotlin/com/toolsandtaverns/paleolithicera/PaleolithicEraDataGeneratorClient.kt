package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.datagen.loot.LootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.ModAdvancementProvider
import com.toolsandtaverns.paleolithicera.datagen.ModEntityTypeTagProvider
import com.toolsandtaverns.paleolithicera.datagen.ModItemTagProvider
import com.toolsandtaverns.paleolithicera.datagen.ModModelProvider
import com.toolsandtaverns.paleolithicera.datagen.ModRegistryDataGenerator
import com.toolsandtaverns.paleolithicera.datagen.ModBlockTagProvider
import com.toolsandtaverns.paleolithicera.datagen.loot.EntityLootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.EdiblePlantRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.KnappingRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.VanillaRecipeProvider
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
     * @param fabricDataGenerator The data generator provided by Fabric
     */
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()
        // Add item tag generator for creating item tags
        pack.addProvider(::ModItemTagProvider)
        // Add recipe generators for creating vanilla-style crafting recipes
        pack.addProvider(::VanillaRecipeProvider)
        pack.addProvider(::KnappingRecipeProvider)
        pack.addProvider(::EdiblePlantRecipeProvider)
        // Add loot table generator for creating loot tables
        pack.addProvider(::LootTableProvider)
        pack.addProvider(::EntityLootTableProvider)
        // Add registry data generator for other registry data
        pack.addProvider(::ModRegistryDataGenerator)
        // Add model generator for creating block and item models (only one allowed)
        pack.addProvider(::ModModelProvider)
        // Add block tag generator for creating block tags
        pack.addProvider(::ModBlockTagProvider)
        // Add entity type tag generator for creating entity type tags
        pack.addProvider(::ModEntityTypeTagProvider)
        // Add advancement generator for creating advancements
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