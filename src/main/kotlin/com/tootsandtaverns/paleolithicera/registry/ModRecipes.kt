package com.tootsandtaverns.paleolithicera.registry

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import com.tootsandtaverns.paleolithicera.PaleolithicEra.logger
import com.tootsandtaverns.paleolithicera.recipe.KnapRecipe
import com.tootsandtaverns.paleolithicera.recipe.KnapRecipeSerializer
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