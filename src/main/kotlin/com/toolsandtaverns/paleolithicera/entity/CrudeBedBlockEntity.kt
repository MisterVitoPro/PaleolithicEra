package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos

/**
 * Crude bed block entity used by the CrudeBedBlock.
 * This implementation currently holds no extra data but is required to support a multi-block bed structure.
 */
class CrudeBedBlockEntity(
    pos: BlockPos,
    state: BlockState?
) : BlockEntity(ModEntities.CRUDE_BED, pos, state) {

    override fun toUpdatePacket(): BlockEntityUpdateS2CPacket? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

}
