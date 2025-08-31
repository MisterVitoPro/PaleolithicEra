package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.datagen.ModAdvancementProvider
import com.toolsandtaverns.paleolithicera.datagen.ModDynamicRegistryProvider
import com.toolsandtaverns.paleolithicera.datagen.ModModelProvider
import com.toolsandtaverns.paleolithicera.datagen.loot.EntityLootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.loot.ModBlockLootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.EdiblePlantRecipeProvider
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

object PaleolithicEraDataGeneratorClient : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()
        pack.addProvider(::ModItemTagProvider)
        pack.addProvider(::VanillaRecipeProvider)
        pack.addProvider(::KnappingRecipeProvider)
        pack.addProvider(::ModModelProvider)
        pack.addProvider(::EdiblePlantRecipeProvider)
        pack.addProvider(::ModBlockLootTableProvider)
        pack.addProvider(::EntityLootTableProvider)
        pack.addProvider(::ModDynamicRegistryProvider)
        pack.addProvider(::ModBlockTagProvider)
        pack.addProvider(::ModEntityTypeTagProvider)
        pack.addProvider(::ModAdvancementProvider)
    }

    override fun buildRegistry(registryBuilder: RegistryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
    }
}