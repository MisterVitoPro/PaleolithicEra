package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.screen.GroundStorageScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
import net.minecraft.util.math.BlockPos

/**
 * Block entity for Ground Storage. Stores up to 8 stackable items.
 */
class GroundStorageBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModEntityType.GROUND_STORAGE_BLOCK_ENTITY, pos, state),
    ExtendedScreenHandlerFactory<BlockPos> {

    companion object { const val SLOT_COUNT = 8 }

    val inventory: SimpleInventory = object : SimpleInventory(SLOT_COUNT) {
        override fun canInsert(stack: ItemStack): Boolean {
            // Only allow items that are stackable (maxCount > 1)
            return stack.isStackable
        }

        override fun markDirty() {
            super.markDirty()
            this@GroundStorageBlockEntity.markDirty()
        }
    }

    override fun getDisplayName(): Text = Text.translatable("block.paleolithic-era.ground_storage")

    override fun getScreenOpeningData(player: ServerPlayerEntity): BlockPos = this.pos

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler = GroundStorageScreenHandler(syncId, playerInventory, this)

    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }

    override fun readData(view: net.minecraft.storage.ReadView) {
        super.readData(view)
        Inventories.readData(view, inventory.heldStacks)
    }

    override fun writeData(view: net.minecraft.storage.WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> =
        BlockEntityUpdateS2CPacket.create(this)

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup) =
        createNbt(registries)
}
