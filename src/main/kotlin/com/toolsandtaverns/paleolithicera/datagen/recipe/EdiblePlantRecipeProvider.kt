package com.toolsandtaverns.paleolithicera.datagen.recipe

import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.data.recipe.CookingRecipeJsonBuilder
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Generates recipes for edible plants.
 * - If cookable=true, we generate a cooking recipe.
 */
class EdiblePlantRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {

        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                EdiblePlants.entries.forEach { plant ->
                    val def = plant.definitions
                    if (def.cookable && def.cookedIdPath != null) {
                        val id = id(def.idPath)
                        val cookedId = id(def.cookedIdPath)
                        val item = Registries.ITEM.get(id)
                        val cookedItem = Registries.ITEM.get(cookedId)

                        CookingRecipeJsonBuilder.createSmelting(
                            Ingredient.ofItems(item),   // Input item
                            RecipeCategory.FOOD,              // Recipe category for JEI/REI
                            cookedItem,                         // Output item
                            0.35f,                          // Experience given
                            10 * 20                        // Cook time in ticks (20 ticks = 1 second)
                        ).criterion(
                            "has_${def.idPath}",
                            InventoryChangedCriterion.Conditions.items(item)
                        ).offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, id("${def.idPath}_smelt")))

                        CookingRecipeJsonBuilder.createCampfireCooking(
                            Ingredient.ofItems(item),   // Input item
                            RecipeCategory.FOOD,              // Recipe category for JEI/REI
                            cookedItem,                         // Output item
                            0.35f,                          // Experience given
                            10 * 20                        // Cook time in ticks (20 ticks = 1 second)
                        ).criterion(
                            "has_${def.idPath}",
                            InventoryChangedCriterion.Conditions.items(item)
                        ).offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, id("${def.idPath}_campfire_cooking")))
                    }
                }
            }
        }
    }

    override fun getName(): String? {
        return "Edible Plant RecipeProvider"
    }

}
