package com.toolsandtaverns.paleolithicera.integration

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import net.minecraft.Bootstrap
import net.minecraft.SharedConstants
import net.minecraft.registry.Registries
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("p1")
@Tag("integration")
class RecipeRegistryTest {

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
    fun `custom recipe serializers and types use matching mod identifiers`() {
        assertEquals(
            "$MOD_ID:knapping",
            Registries.RECIPE_SERIALIZER.getId(ModRecipes.KNAPPING_SERIALIZER).toString()
        )
        assertEquals(
            "$MOD_ID:knapping",
            Registries.RECIPE_TYPE.getId(ModRecipes.KNAPPING_RECIPE_TYPE).toString()
        )
        assertEquals(
            "$MOD_ID:food_drying",
            Registries.RECIPE_SERIALIZER.getId(ModRecipes.FOOD_DRYING_SERIALIZER).toString()
        )
        assertEquals(
            "$MOD_ID:food_drying",
            Registries.RECIPE_TYPE.getId(ModRecipes.FOOD_DRYING_RECIPE_TYPE).toString()
        )
    }
}
