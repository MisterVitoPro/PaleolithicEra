package com.tootsandtaverns.paleolithicera.recipe

import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.RecipeInput

class KnapRecipeInput(val input: ItemStack) : RecipeInput {

    override fun getStackInSlot(slot: Int): ItemStack? {
        return input
    }

    override fun size(): Int {
        return 4
    }
}