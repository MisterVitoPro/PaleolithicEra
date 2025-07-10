package com.tootsandtaverns.paleolithicera.recipe

import com.tootsandtaverns.paleolithicera.registry.ModRecipes
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
import java.util.Optional
import kotlin.jvm.optionals.toList

class KnapRecipe(
    val output: ItemStack,
    val inputs: List<Ingredient>,
    val isShaped: Boolean = false
) : Recipe<KnapRecipeInput> {

    constructor(
        output: ItemStack,
        vararg inputs: ItemConvertible,
        isShaped: Boolean = false
    ) : this(
        output,
        inputs.map { Ingredient.ofItems(it) },
        isShaped
    )

    private val inputIngredients: List<Optional<Ingredient>> = inputs.map { Optional.of(it) }.toList()

    /**
     * Checks shaped matching – input must match ingredients slot-by-slot.
     */
    private fun matchesShaped(input: KnapRecipeInput): Boolean {
        if (input.size() != inputIngredients.size) return false

        for (i in 0 until inputIngredients.size) {
            if (!inputIngredients[i].get().test(input.getStackInSlot(i))) {
                return false
            }
        }
        return true
    }

    /**
     * Checks shapeless matching – input items must match recipe ingredients in any order.
     */
    private fun matchesShapeless(input: KnapRecipeInput): Boolean {
        val remaining = inputIngredients.toMutableList()

        for (stack in input.getStacks()) {
            if (stack.isEmpty) continue
            val matchIndex = remaining.indexOfFirst { it.get().test(stack) }
            if (matchIndex >= 0) {
                remaining.removeAt(matchIndex)
            } else {
                return false // Extra unmatched input
            }
        }

        return remaining.isEmpty()
    }

    override fun matches(input: KnapRecipeInput, world: World): Boolean {
        if (world.isClient) return false

        return if (isShaped) {
            matchesShaped(input)
        } else {
            matchesShapeless(input)
        }
    }

    override fun craft(
        input: KnapRecipeInput?,
        registries: RegistryWrapper.WrapperLookup?
    ): ItemStack? {
        return output.copy()
    }

    override fun getSerializer(): KnapRecipeSerializer? = ModRecipes.KNAPPING_SERIALIZER

    override fun getType(): RecipeType<out Recipe<KnapRecipeInput>> = ModRecipes.KNAPPING_RECIPE_TYPE

    override fun getIngredientPlacement(): IngredientPlacement {
        return IngredientPlacement.forMultipleSlots(
            inputIngredients
        )
    }

    override fun getRecipeBookCategory(): RecipeBookCategory {
        return RecipeBookCategories.CRAFTING_MISC
    }

    fun getRawInputs(): List<Ingredient> {
        return inputIngredients.map { it.get() }.toList()
    }
}

