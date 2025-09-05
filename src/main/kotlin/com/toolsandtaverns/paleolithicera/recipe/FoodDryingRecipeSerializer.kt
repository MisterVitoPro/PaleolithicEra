package com.toolsandtaverns.paleolithicera.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer

object FoodDryingRecipeSerializer : RecipeSerializer<FoodDryingRecipe> {

    val CODEC: MapCodec<FoodDryingRecipe> =
        RecordCodecBuilder.mapCodec { inst: RecordCodecBuilder.Instance<FoodDryingRecipe> ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(FoodDryingRecipe::ingredient),
                ItemStack.CODEC.fieldOf("result").forGetter(FoodDryingRecipe::output),
            ).apply(inst, ::FoodDryingRecipe)
        }

    val PACKET_CODEC: PacketCodec<RegistryByteBuf, FoodDryingRecipe> =
        PacketCodec.tuple(
            Ingredient.PACKET_CODEC,
            FoodDryingRecipe::ingredient,
            ItemStack.PACKET_CODEC,
            FoodDryingRecipe::output,
            ::FoodDryingRecipe
        )

    override fun codec(): MapCodec<FoodDryingRecipe> = CODEC

    @Deprecated("Deprecated in Java")
    override fun packetCodec(): PacketCodec<RegistryByteBuf, FoodDryingRecipe> = PACKET_CODEC
}