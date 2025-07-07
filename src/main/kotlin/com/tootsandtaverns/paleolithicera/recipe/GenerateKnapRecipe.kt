package com.tootsandtaverns.paleolithicera.recipe

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.item.ItemStack
import net.minecraft.recipe.IngredientPlacement
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.spongepowered.include.com.google.gson.JsonObject
import org.spongepowered.include.com.google.gson.JsonPrimitive

/**
 * A generated knapping recipe for data generation that emits JSON and advancement unlocks.
 */
class GeneratedKnapRecipe(
    private val recipeId: Identifier,
    private val inputItem: String,
    private val outputItem: String,
    private val outputCount: Int
) : Recipe<CraftingRecipeInput> {

    override fun getType(): RecipeType<out Recipe<CraftingRecipeInput>> = RecipeType.CRAFTING

    override fun getSerializer(): RecipeSerializer<out Recipe<CraftingRecipeInput>> = RecipeSerializer.SHAPELESS

    override fun matches(input: CraftingRecipeInput, world: World): Boolean = false

    override fun craft(input: CraftingRecipeInput, registries: RegistryWrapper.WrapperLookup): ItemStack = ItemStack.EMPTY

    override fun isIgnoredInRecipeBook(): Boolean = false

    override fun showNotification(): Boolean = true

    override fun getGroup(): String = ""

    override fun getIngredientPlacement(): IngredientPlacement? = null

    override fun getRecipeBookCategory(): RecipeBookCategory? = null

    /** Emits JSON for the recipe file. */
    fun getRecipeJson(): JsonObject {
        return JsonObject().apply {
            add("type", JsonPrimitive("paleolithic-era:knapping"))
            add("input", JsonPrimitive(inputItem))
            add("result", JsonObject().apply {
                add("item", JsonPrimitive(outputItem))
                add("count", JsonPrimitive(outputCount))
            })
        }
    }

    /** Returns an advancement entry to unlock this recipe in the recipe book when the player gets the input item. */
    fun getAdvancementEntry(): AdvancementEntry {
        val advancementId = Identifier.of(recipeId.namespace, "recipes/knapping/${recipeId.path}")
        val inputIdentifier = Identifier.tryParse(inputItem)
        val item = Registries.ITEM.get(inputIdentifier ?: Identifier.of("minecraft", "air"))

        val builder = Advancement.Builder.create()
            .criterion("has_input", InventoryChangedCriterion.Conditions.items(item))
            .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, recipeId)))
            .build(advancementId)

        return builder
    }
}