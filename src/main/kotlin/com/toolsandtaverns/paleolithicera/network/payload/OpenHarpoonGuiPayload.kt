package com.toolsandtaverns.paleolithicera.network.payload

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id
import net.minecraft.network.packet.CustomPayload.Type

object OpenHarpoonGuiPayload : CustomPayload {
    val ID = id("open_harpoon_gui")

    override fun getId(): Id<OpenHarpoonGuiPayload> =
        Id(ID)

    val TYPE: Type<PacketByteBuf, OpenHarpoonGuiPayload> =
        Type(getId(), PacketCodec.unit(this))

}
