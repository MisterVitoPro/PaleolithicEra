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
                Ingredient.CODEC.listOf().fieldOf("ingredient")
                    .forGetter { it.getRawInputs() },
                ItemStack.CODEC.fieldOf("result")
                    .forGetter(KnapRecipe::output),
                Codec.BOOL.optionalFieldOf("isShaped", false)
                    .forGetter { it.isShaped }
            ).apply(inst) { ingredients, result, isShaped ->
                KnapRecipe(result, ingredients, isShaped)
            }
        }

    val PACKET_CODEC: PacketCodec<RegistryByteBuf, KnapRecipe> =
        PacketCodec.tuple(
            PacketCodecs.collection({ ArrayList() }, Ingredient.PACKET_CODEC),
            KnapRecipe::getRawInputs,

            ItemStack.PACKET_CODEC,
            KnapRecipe::output,

            PacketCodecs.BOOLEAN,
            KnapRecipe::isShaped
        ) { ingredients: List<Ingredient>, output: ItemStack, isShaped: Boolean ->
            KnapRecipe(output, ingredients, isShaped)
        }

    override fun codec(): MapCodec<KnapRecipe> = CODEC

    @Deprecated("Deprecated in Java")
    override fun packetCodec(): PacketCodec<RegistryByteBuf, KnapRecipe> = PACKET_CODEC
}