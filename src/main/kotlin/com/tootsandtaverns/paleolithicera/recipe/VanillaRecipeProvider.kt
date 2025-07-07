package com.tootsandtaverns.paleolithicera.recipe

import com.tootsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class VanillaRecipeProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) : FabricRecipeProvider(output, registriesFuture) {

    override fun getRecipeGenerator(registryLookup: RegistryWrapper.WrapperLookup, exporter: RecipeExporter): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                createShaped(RecipeCategory.COMBAT, ModItems.BONE_SPEAR, 1)
                    .pattern("  B")
                    .pattern(" S ")
                    .pattern("S  ")
                    .input('B', Items.BONE)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.BONE_SPEAR))
                    .offerTo(exporter)
            }
        }
    }

    override fun getName(): String {
        return "VanillaRecipeProvider"
    }
}