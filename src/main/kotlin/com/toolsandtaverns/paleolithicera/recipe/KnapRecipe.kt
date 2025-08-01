package com.toolsandtaverns.paleolithicera.recipe

import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.IngredientPlacement
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategories
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.World
import java.util.*

class KnapRecipe(
    val inputItem: Ingredient,
    val output: ItemStack
) : Recipe<KnapRecipeInput> {

    override fun matches(input: KnapRecipeInput, world: World): Boolean {
        if (world.isClient) return false

        return inputItem.test(input.getStackInSlot(0))
    }

    override fun craft(
        input: KnapRecipeInput,
        registries: RegistryWrapper.WrapperLookup?
    ): ItemStack {
        return output.copy()
    }

    override fun getSerializer(): KnapRecipeSerializer? = ModRecipes.KNAPPING_SERIALIZER

    override fun getType(): RecipeType<out Recipe<KnapRecipeInput>> = ModRecipes.KNAPPING_RECIPE_TYPE

    override fun getIngredientPlacement(): IngredientPlacement {
        return IngredientPlacement.forSingleSlot(inputItem)
    }

    override fun getRecipeBookCategory(): RecipeBookCategory {
        return RecipeBookCategories.CRAFTING_MISC
    }

}

