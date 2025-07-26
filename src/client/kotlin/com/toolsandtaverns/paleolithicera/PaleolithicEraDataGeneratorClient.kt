package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.datagen.loot.LootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.ModAdvancementProvider
import com.toolsandtaverns.paleolithicera.datagen.ModModelProvider
import com.toolsandtaverns.paleolithicera.datagen.ModRegistryDataGenerator
import com.toolsandtaverns.paleolithicera.datagen.PaleolithicBlockTagProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.KnappingRecipeProvider
import com.toolsandtaverns.paleolithicera.datagen.recipe.VanillaRecipeProvider
import com.toolsandtaverns.paleolithicera.world.ModConfiguredFeatures
import com.toolsandtaverns.paleolithicera.world.ModPlacedFeatures
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys

object PaleolithicEraDataGeneratorClient : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()
        pack.addProvider(::VanillaRecipeProvider)
        pack.addProvider(::KnappingRecipeProvider)
        pack.addProvider(::LootTableProvider)
        pack.addProvider(::ModRegistryDataGenerator)
        pack.addProvider(::ModModelProvider)
        pack.addProvider { output, registriesFuture ->
            PaleolithicBlockTagProvider(output, registriesFuture)
        }
        pack.addProvider(::ModAdvancementProvider)
    }

    override fun buildRegistry(registryBuilder: RegistryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
    }

}