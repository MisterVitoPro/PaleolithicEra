package com.toolsandtaverns.paleolithicera.network.payload

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id
import net.minecraft.network.packet.CustomPayload.Type

data class HarpoonResultPayload(val success: Boolean) : CustomPayload {
    override fun getId(): Id<HarpoonResultPayload> = ID

    companion object {
        val ID: Id<HarpoonResultPayload> = Id(id("harpoon_fish_result"))

        val CODEC: PacketCodec<PacketByteBuf, HarpoonResultPayload> =
            PacketCodec.of(
                { payload, buf -> buf.writeBoolean(payload.success) },
                { buf -> HarpoonResultPayload(buf.readBoolean()) }
            )

        val TYPE: Type<PacketByteBuf, HarpoonResultPayload> = Type(ID, CODEC)
    }
}
