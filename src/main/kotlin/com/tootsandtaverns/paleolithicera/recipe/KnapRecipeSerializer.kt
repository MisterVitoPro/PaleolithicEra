package com.tootsandtaverns.paleolithicera.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier

object KnapRecipeSerializer : RecipeSerializer<KnapRecipe> {
    val CODEC: MapCodec<KnapRecipe> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(KnapRecipe::id),
            ItemStack.CODEC.fieldOf("input").forGetter { it.inputItem },
            ItemStack.CODEC.fieldOf("result").forGetter { it.output }
        ).apply(instance, ::KnapRecipe)
    }

    val PACKET_CODEC: PacketCodec<RegistryByteBuf, KnapRecipe> =
        PacketCodec.of(
            { recipe, buf ->
                Identifier.PACKET_CODEC.encode(buf, recipe.id)
                ItemStack.PACKET_CODEC.encode(buf, recipe.inputItem)
                ItemStack.PACKET_CODEC.encode(buf, recipe.output)
            },
            { buf ->
                val id = Identifier.PACKET_CODEC.decode(buf)
                val input = ItemStack.PACKET_CODEC.decode(buf)
                val output = ItemStack.PACKET_CODEC.decode(buf)
                KnapRecipe(id, output, input)
            }
        )

    override fun codec(): MapCodec<KnapRecipe> = CODEC

    @Deprecated("Deprecated in Java")
    override fun packetCodec(): PacketCodec<RegistryByteBuf, KnapRecipe> = PACKET_CODEC
}