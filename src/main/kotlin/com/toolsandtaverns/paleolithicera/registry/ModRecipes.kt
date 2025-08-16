package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipe
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipeSerializer
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModRecipes {
    val KNAPPING_SERIALIZER: KnapRecipeSerializer =
        Registry.register(Registries.RECIPE_SERIALIZER, id("knapping"), KnapRecipeSerializer)
    val KNAPPING_RECIPE_TYPE: RecipeType<KnapRecipe> =
        Registry.register(Registries.RECIPE_TYPE, id("knapping"), object : RecipeType<KnapRecipe> {})

    fun initialize() {
        LOGGER.info("Registering Custom Recipes for $MOD_ID")
    }
}