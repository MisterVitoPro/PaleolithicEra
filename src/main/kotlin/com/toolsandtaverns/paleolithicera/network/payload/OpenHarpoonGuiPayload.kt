package com.toolsandtaverns.paleolithicera.network.payload

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id
import net.minecraft.network.packet.CustomPayload.Type

data class OpenHarpoonGuiPayload(
    val attemptId: Long,
    val startTick: Long,
    val targetStartStep: Int
) : CustomPayload {
    override fun getId(): Id<OpenHarpoonGuiPayload> = ID

    companion object {
        val ID: Id<OpenHarpoonGuiPayload> = Id(id("open_harpoon_gui"))

        val CODEC: PacketCodec<PacketByteBuf, OpenHarpoonGuiPayload> =
            PacketCodec.of(
                { payload, buf ->
                    buf.writeLong(payload.attemptId)
                    buf.writeLong(payload.startTick)
                    buf.writeVarInt(payload.targetStartStep)
                },
                { buf ->
                    OpenHarpoonGuiPayload(
                        buf.readLong(),
                        buf.readLong(),
                        buf.readVarInt()
                    )
                }
            )

        val TYPE: Type<PacketByteBuf, OpenHarpoonGuiPayload> = Type(ID, CODEC)
    }
}
