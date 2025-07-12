package com.toolsandtaverns.paleolithicera.util

import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

/**
 * A helper interface to simplify inventory logic in BlockEntities.
 */
interface ImplementedInventory : Inventory {

    /**
     * Gets the backing item list for the inventory.
     */
    fun getItems(): DefaultedList<ItemStack>

    override fun size(): Int = getItems().size

    override fun isEmpty(): Boolean = getItems().all { it.isEmpty }

    override fun getStack(slot: Int): ItemStack = getItems()[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack =
        Inventories.splitStack(getItems(), slot, amount)

    override fun removeStack(slot: Int): ItemStack =
        Inventories.removeStack(getItems(), slot)

    override fun setStack(slot: Int, stack: ItemStack) {
        getItems()[slot] = stack
        if (stack.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    override fun clear() = getItems().clear()
}