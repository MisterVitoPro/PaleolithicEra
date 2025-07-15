package com.toolsandtaverns.paleolithicera.block.entity

import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import com.toolsandtaverns.paleolithicera.screen.KnappingScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

/**
 * Block entity that stores inventory and handles logic for the Knapping Station.
 */
class KnappingStationBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModBlockEntities.KNAPPING_STATION, pos, state),
    NamedScreenHandlerFactory {

    private val inventory = SimpleInventory(5)

    override fun readData(view: ReadView) {
        val list: DefaultedList<ItemStack> = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
        Inventories.readData(view, list)
        for (i in 0 until list.size) {
            inventory.setStack(i, list[i])
        }
    }

    override fun writeData(view: WriteView) {
        val list: DefaultedList<ItemStack> = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
        for (i in 0 until inventory.size()) {
            list[i] = inventory.getStack(i)
        }
        Inventories.writeData(view, list)
    }

    fun getInventory(): SimpleInventory = inventory

    override fun getDisplayName(): Text? {
        return Text.translatable("Knapping Station")
    }

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler? {
        return KnappingScreenHandler(syncId, playerInventory, getInventory())
    }
}
