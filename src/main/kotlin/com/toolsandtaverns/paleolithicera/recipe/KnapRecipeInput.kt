package com.toolsandtaverns.paleolithicera.recipe

import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.recipe.input.RecipeInput

class KnapRecipeInput(val inputItemStacks: List<ItemStack>) : RecipeInput {

    val isShaped: Boolean = false

    override fun getStackInSlot(slot: Int): ItemStack {
        return inputItemStacks.getOrElse(slot) { ItemStack.EMPTY }
    }

    override fun size(): Int {
        return inputItemStacks.size
    }

    override fun isEmpty(): Boolean {
        return inputItemStacks.all { it.isEmpty }
    }

    fun getStacks(): List<ItemStack> {
        return inputItemStacks
    }
}