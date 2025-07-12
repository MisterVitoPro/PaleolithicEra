package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.PaleolithicEra.logger
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipe
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModRecipes {
    val KNAPPING_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "knapping"), KnapRecipeSerializer)
    val KNAPPING_RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "knapping"), object : RecipeType<KnapRecipe>{})

    fun initialize() {
        logger.info("Registering Custom Recipes for $MOD_ID")
    }
}