package com.tootsandtaverns.paleolithicera.recipe

import com.tootsandtaverns.paleolithicera.PaleolithicEra.logger
import com.tootsandtaverns.paleolithicera.registry.ModRecipes
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.IngredientPlacement
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategories
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.world.World

class KnapRecipe(
    val output: ItemStack,
    val inputItem: Ingredient
) : Recipe<KnapRecipeInput> {

    override fun matches(
        input: KnapRecipeInput,
        world: World
    ): Boolean {
        if (world.isClient())
            return false

        // Match: at least one of the first 4 slots contains the input item
        for (i in 0 until 4) {
            logger.info("Test Match for Slot $i="+inputItem.test(input.getStackInSlot(i)))
            val stack = input.getStackInSlot(i)
            if (!stack.isEmpty && inputItem.test(input.getStackInSlot(i))) {
                return true
            }
        }
        return false
    }

    override fun craft(
        input: KnapRecipeInput?,
        registries: RegistryWrapper.WrapperLookup?
    ): ItemStack? {
        return output.copy()
    }

    override fun getSerializer(): KnapRecipeSerializer? = ModRecipes.KNAPPING_SERIALIZER

    override fun getType(): RecipeType<out Recipe<KnapRecipeInput>> = ModRecipes.KNAPPING_RECIPE_TYPE

    override fun getIngredientPlacement(): IngredientPlacement? {
        return IngredientPlacement.forSingleSlot(inputItem)
    }

    override fun getRecipeBookCategory(): RecipeBookCategory? {
        return RecipeBookCategories.CRAFTING_MISC
    }
}

