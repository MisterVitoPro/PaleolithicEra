package com.toolsandtaverns.paleolithicera.recipe

import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.RecipeInput

class KnapRecipeInput(val input: ItemStack) : RecipeInput {

    override fun getStackInSlot(slot: Int): ItemStack {
        require(slot == 0) { "No item for slot $slot" }
        return input
    }

    override fun size(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return input.isEmpty
    }

}
