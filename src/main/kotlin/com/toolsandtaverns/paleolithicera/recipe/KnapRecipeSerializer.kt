package com.toolsandtaverns.paleolithicera.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer

object KnapRecipeSerializer : RecipeSerializer<KnapRecipe> {

    val CODEC: MapCodec<KnapRecipe> =
        RecordCodecBuilder.mapCodec { inst: RecordCodecBuilder.Instance<KnapRecipe> ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(KnapRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(KnapRecipe::output),
            ).apply(inst, ::KnapRecipe)
        }

    val PACKET_CODEC: PacketCodec<RegistryByteBuf, KnapRecipe> =
        PacketCodec.tuple(
            Ingredient.PACKET_CODEC,
            KnapRecipe::inputItem,

            ItemStack.PACKET_CODEC,
            KnapRecipe::output,
            ::KnapRecipe)

    override fun codec(): MapCodec<KnapRecipe> = CODEC

    @Deprecated("Deprecated in Java")
    override fun packetCodec(): PacketCodec<RegistryByteBuf, KnapRecipe> = PACKET_CODEC
}