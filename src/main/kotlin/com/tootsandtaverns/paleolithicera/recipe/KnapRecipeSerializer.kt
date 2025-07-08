package com.tootsandtaverns.paleolithicera.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.tootsandtaverns.paleolithicera.recipe.KnapRecipeSerializer.CODEC
import com.tootsandtaverns.paleolithicera.recipe.KnapRecipeSerializer.PACKET_CODEC
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import java.util.function.BiFunction
import java.util.function.Function

object KnapRecipeSerializer : RecipeSerializer<KnapRecipe> {

    val CODEC: MapCodec<KnapRecipe> =
        RecordCodecBuilder.mapCodec { inst: RecordCodecBuilder.Instance<KnapRecipe> ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient")
                    .forGetter(KnapRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result")
                    .forGetter(KnapRecipe::output)
            ).apply(
                inst
            ) { inputItem: Ingredient, output: ItemStack -> KnapRecipe(output, inputItem) }
        }

    val PACKET_CODEC: PacketCodec<RegistryByteBuf, KnapRecipe> = 
        PacketCodec.tuple(
            Ingredient.PACKET_CODEC, KnapRecipe::inputItem,
            ItemStack.PACKET_CODEC, KnapRecipe::output) { inputItem: Ingredient, output: ItemStack -> KnapRecipe(output, inputItem) }

    override fun codec(): MapCodec<KnapRecipe> = CODEC

    @Deprecated("Deprecated in Java")
    override fun packetCodec(): PacketCodec<RegistryByteBuf, KnapRecipe> = PACKET_CODEC
}