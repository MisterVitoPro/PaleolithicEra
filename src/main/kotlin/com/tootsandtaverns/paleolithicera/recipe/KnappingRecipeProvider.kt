package com.tootsandtaverns.paleolithicera.recipe

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import com.tootsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.RegistryKeys
import java.util.concurrent.CompletableFuture

/**
 * Data provider that generates knapping recipes for the Knapping Station.
 */
class KnappingRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                val recipeId = Identifier.of(MOD_ID, "knap_flint_to_flint_shard")
                val resultStack = ItemStack(ModItems.FLINT_SHARD) // Replace with your custom output
                val inputStack = ItemStack(Items.FLINT)

                val recipe = KnapRecipe(resultStack, Ingredient.ofItems(inputStack.item))
                val key = RegistryKey.of(RegistryKeys.RECIPE, recipeId)

                exporter.accept(key, recipe, null)
            }
        }
    }

    override fun getName(): String? {
        return "Knapping Recipe Provider"
    }
}
