package com.toolsandtaverns.paleolithicera.recipe

import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.IngredientPlacement
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategories
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.World

/**
 * Recipe for drying food items in the Food Dryer.
 * 
 * This recipe type defines the transformation from raw/fresh food items
 * to their dried preservation variants. Food drying was crucial for
 * early human survival, allowing food to be stored for extended periods.
 */
class FoodDryingRecipe(
    val ingredient: Ingredient,
    val output: ItemStack
) : Recipe<FoodDryingRecipeInput> {

    override fun matches(input: FoodDryingRecipeInput, world: World): Boolean {
        if (world.isClient) return false
        return ingredient.test(input.getStackInSlot(0))
    }

    override fun craft(
        input: FoodDryingRecipeInput?,
        registries: RegistryWrapper.WrapperLookup?
    ): ItemStack {
        return output.copy()
    }

    override fun getSerializer(): FoodDryingRecipeSerializer {
        return ModRecipes.FOOD_DRYING_SERIALIZER
    }

    override fun getType(): RecipeType<out Recipe<FoodDryingRecipeInput>> {
        return ModRecipes.FOOD_DRYING_RECIPE_TYPE
    }

    override fun getIngredientPlacement(): IngredientPlacement {
        return IngredientPlacement.forSingleSlot(ingredient)
    }

    override fun getRecipeBookCategory(): RecipeBookCategory {
        return RecipeBookCategories.CRAFTING_MISC
    }
}