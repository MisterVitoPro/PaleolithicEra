package com.toolsandtaverns.paleolithicera.datagen.recipe

import com.toolsandtaverns.paleolithicera.recipe.FoodDryingRecipe
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Generates all food drying recipes for the Food Dryer block.
 * 
 * This provider creates recipes that convert raw foods into their dried counterparts
 * using the food dryer block. Dried foods have different nutritional properties
 * than their raw or cooked equivalents.
 */
class FoodDryingRecipeProvider(
    output: FabricDataOutput, 
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getName(): String = "Food Drying Recipes"

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                // Create dried versions of vanilla Minecraft meats
                createMeatDryingRecipe(Items.BEEF, ModItems.DRIED_BEEF)
                createMeatDryingRecipe(Items.PORKCHOP, ModItems.DRIED_PORK)
                createMeatDryingRecipe(Items.CHICKEN, ModItems.DRIED_CHICKEN)
                createMeatDryingRecipe(Items.MUTTON, ModItems.DRIED_MUTTON)
                createMeatDryingRecipe(Items.RABBIT, ModItems.DRIED_RABBIT)
                createMeatDryingRecipe(Items.COD, ModItems.DRIED_COD)
                createMeatDryingRecipe(Items.SALMON, ModItems.DRIED_SALMON)
            }

            private fun createMeatDryingRecipe(input: Item, output: Item) {
                val recipe = FoodDryingRecipe(
                    Ingredient.ofItems(input),
                    output.defaultStack
                )
                
                exporter.accept(
                    net.minecraft.registry.RegistryKey.of(RegistryKeys.RECIPE, id("food_drying/${getRecipeName(output)}")),
                    recipe,
                    null
                )
            }

            private fun getRecipeName(output: Item): String {
                return Registries.ITEM.getId(output).path
            }
        }
    }
}
