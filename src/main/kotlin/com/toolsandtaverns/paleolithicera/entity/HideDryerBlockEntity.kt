package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.screen.HideDryerScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * HideDryerBlockEntity dries Rawhide into Dry Rawhide during the day.
 * Uses a 2-slot SimpleInventory: [0] = input, [1] = output.
 */
class HideDryerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModEntities.HIDE_DRYER_BLOCK_ENTITY, pos, state), ExtendedScreenHandlerFactory<BlockPos> {


    fun tick(world: World) {
        if (world.isClient || !world.isDay) return

        val input = inventory.getStack(0)
        val output = inventory.getStack(1)

        if (input.isOf(ModItems.RAWHIDE) &&
            (output.isEmpty || (output.item == ModItems.DRY_HIDE && output.count < output.maxCount))
        ) {
            progress++
            if (progress >= (DRYING_DURATION_SECS * 20)) {
                input.decrement(1)
                if (output.isEmpty) {
                    inventory.setStack(1, ItemStack(ModItems.DRY_HIDE))
                } else {
                    output.increment(1)
                }
                progress = 0
                markDirty()
            }
        } else {
            progress = 0
        }
    }

    private var progress = 0
    val inventory = SimpleInventory(2)

    val propertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int = when (index) {
            0 -> progress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            if (index == 0) progress = value
        }

        override fun size(): Int = 1
    }

    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }

    override fun getScreenOpeningData(player: ServerPlayerEntity): BlockPos {
        return this.pos
    }

    override fun getDisplayName(): Text? {
        return Text.translatable("block.paleolithic-era.hide_dryer")
    }

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler? {
        return HideDryerScreenHandler(syncId, playerInventory, pos)
    }

    companion object {
        const val DRYING_DURATION_SECS = 15
        const val DRYING_DURATION_TICKS = DRYING_DURATION_SECS * 20
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound = createNbt(registries)
}
