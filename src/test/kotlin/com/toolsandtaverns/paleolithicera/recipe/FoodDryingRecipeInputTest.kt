package com.toolsandtaverns.paleolithicera.recipe

import net.minecraft.Bootstrap
import net.minecraft.SharedConstants
import net.minecraft.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll

@Tag("p0")
@Tag("unit")
class FoodDryingRecipeInputTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrapMinecraft() {
            runCatching(SharedConstants::getGameVersion)
                .getOrElse { SharedConstants.createGameVersion() }
            Bootstrap.initialize()
        }
    }

    @Test
    fun `exposes exactly one slot`() {
        val input = FoodDryingRecipeInput(ItemStack.EMPTY)

        assertEquals(1, input.size())
        assertSame(ItemStack.EMPTY, input.getStackInSlot(0))
    }

    @Test
    fun `rejects indexes outside its single slot`() {
        val input = FoodDryingRecipeInput(ItemStack.EMPTY)

        assertThrows(IllegalArgumentException::class.java) { input.getStackInSlot(-1) }
        assertThrows(IllegalArgumentException::class.java) { input.getStackInSlot(1) }
    }
}
