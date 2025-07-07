package com.tootsandtaverns.paleolithicera

import com.tootsandtaverns.paleolithicera.datagen.LootTableGenerator
import com.tootsandtaverns.paleolithicera.recipe.KnappingRecipeProvider
import com.tootsandtaverns.paleolithicera.recipe.VanillaRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import java.util.concurrent.CompletableFuture

object PaleolithicEraDataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()
		pack.addProvider(::VanillaRecipeProvider)
		pack.addProvider(::KnappingRecipeProvider)

		pack.addProvider(::LootTableGenerator)
	}
}