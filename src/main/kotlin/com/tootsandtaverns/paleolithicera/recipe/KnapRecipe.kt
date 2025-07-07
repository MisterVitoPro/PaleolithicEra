package com.tootsandtaverns.paleolithicera.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.tootsandtaverns.paleolithicera.registry.ModRecipes
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.recipe.IngredientPlacement
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.recipe.input.RecipeInput
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.world.World

class KnapRecipe(
    val id: Identifier,
    val output: ItemStack,
    val inputItem: ItemStack
) : Recipe<RecipeInput> {

    override fun getType(): RecipeType<out Recipe<RecipeInput>> = ModRecipes.KNAPPING_RECIPE_TYPE

    override fun getIngredientPlacement(): IngredientPlacement? {
        return null
    }

    override fun getRecipeBookCategory(): RecipeBookCategory? {
        return null
    }

    override fun matches(
        input: RecipeInput?,
        world: World?
    ): Boolean {
        if (input == null || input.size() < 4) return false

        // Match: at least one of the first 4 slots contains the input item
        for (i in 0 until 4) {
            val stack = input.getStackInSlot(i)
            if (!stack.isEmpty && stack.item == inputItem.item) {
                return true
            }
        }

        return false
    }

    override fun craft(
        input: RecipeInput?,
        registries: RegistryWrapper.WrapperLookup?
    ): ItemStack? {
        return output.copy()
    }

    override fun getSerializer(): KnapRecipeSerializer? = ModRecipes.KNAPPING_SERIALIZER
}

