package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.datagen.loot.LootTableProvider
import com.toolsandtaverns.paleolithicera.datagen.ModAdvancementProvider
import com.toolsandtaverns.paleolithicera.recipe.KnappingRecipeProvider
import com.toolsandtaverns.paleolithicera.recipe.VanillaRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object PaleolithicEraDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()
        pack.addProvider(::VanillaRecipeProvider)
        pack.addProvider(::KnappingRecipeProvider)

        pack.addProvider(::LootTableProvider)
        pack.addProvider(::ModAdvancementProvider)
    }
}