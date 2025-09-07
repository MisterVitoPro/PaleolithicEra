package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.recipe.FoodDryingRecipe
import com.toolsandtaverns.paleolithicera.recipe.FoodDryingRecipeInput
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import com.toolsandtaverns.paleolithicera.screen.FoodDryerScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.recipe.RecipeEntry
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Optional
import kotlin.math.min

/**
 * FoodDryerBlockEntity implements the food drying process for preserving food items.
 * 
 * The Food Dryer represents an essential food preservation technology that allows early humans
 * to store food for longer periods. This block entity manages a 4-slot drying system where
 * each slot operates independently with its own progress timer.
 * 
 * Key gameplay features:
 * - 4 independent drying slots, each accepts only 1 item
 * - Recipe-based system determines valid inputs and outputs
 * - Individual progress tracking per slot
 * - Items are visually rendered hanging on the dryer structure
 * - Drying speed affected by weather and time of day
 * - Provides essential food preservation for survival gameplay
 */
class FoodDryerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModEntityType.FOOD_DRYER_BLOCK_ENTITY, pos, state), ExtendedScreenHandlerFactory<BlockPos> {

    /**
     * 4-slot inventory for food items to be dried.
     * Each slot holds maximum 1 item to simulate individual drying spaces.
     */
    val inventory = object : SimpleInventory(SLOT_COUNT) {
        override fun getMaxCountPerStack(): Int = 1 // Each slot can only hold 1 item
        
        override fun canInsert(stack: ItemStack): Boolean {
            return isValidInput(stack)
        }
    }

    /**
     * Individual progress tracking for each slot (0 to DRYING_DURATION_TICKS).
     * Each slot can be at a different stage of the drying process.
     */
    private val slotProgress = FloatArray(SLOT_COUNT) { 0f }

    /**
     * Property delegate exposing progress values to the screen handler.
     * This allows the GUI to display individual progress bars for each slot.
     */
    val propertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int = when {
            index < SLOT_COUNT -> slotProgress[index].toInt()
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            if (index < SLOT_COUNT) {
                slotProgress[index] = value.toFloat()
            }
        }

        override fun size(): Int = SLOT_COUNT
    }

    /**
     * Server-side tick method that handles the drying process for all slots.
     * 
     * Each slot is processed independently:
     * - Checks if slot contains valid input item
     * - Verifies drying recipe exists for the item
     * - Updates progress based on environmental conditions
     * - Completes drying when progress reaches threshold
     * - Environmental factors affect drying speed (rain stops drying, night slows it)
     */
    fun tick(world: ServerWorld) {
        var anySlotChanged = false

        for (slot in 0 until SLOT_COUNT) {
            val stack = inventory.getStack(slot)
            if (stack.isEmpty) {
                // Reset progress for empty slots
                if (slotProgress[slot] > 0f) {
                    slotProgress[slot] = 0f
                    anySlotChanged = true
                }
                continue
            }

            // Find recipe for this item
            val recipe = findRecipeForItem(stack, world)
            if (recipe == null) {
                // Reset progress for invalid items
                if (slotProgress[slot] > 0f) {
                    slotProgress[slot] = 0f
                    anySlotChanged = true
                }
                continue
            }

            // Calculate drying speed based on conditions
            val dryingSpeed = calculateDryingSpeed(world)
            if (dryingSpeed <= 0f) {
                continue // No progress under these conditions
            }

            slotProgress[slot] += dryingSpeed
            anySlotChanged = true

            // Complete drying if progress reached
            if (slotProgress[slot] >= DRYING_DURATION_TICKS) {
                val recipeInput = FoodDryingRecipeInput(stack)
                val output = recipe.craft(recipeInput, world.registryManager)
                inventory.setStack(slot, output)
                slotProgress[slot] = 0f
            }
        }

        if (anySlotChanged) {
            markDirty()
            // Sync to client for rendering updates
            world.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL)
        }
    }

    /**
     * Calculates the drying speed based on environmental conditions.
     * Returns a multiplier for the base drying progress per tick.
     */
    private fun calculateDryingSpeed(world: ServerWorld): Float {
        // Stop completely if raining and dryer is exposed to sky
        if (world.isRaining && world.isSkyVisible(pos)) {
            return 0f
        }

        // Base speed is 1.0 during day, 0.25 at night (like hide dryer)
        return if (world.isDay) 1.0f else 0.25f
    }

    /**
     * Finds a food drying recipe for the given item stack.
     */
    private fun findRecipeForItem(stack: ItemStack, world: World): FoodDryingRecipe? {
        if (world.isClient) return null
        
        // Create a simple recipe input for matching
        val recipeInput = FoodDryingRecipeInput(stack)
        
        // Use getFirstMatch to find a matching recipe
        return (world as ServerWorld).recipeManager
            .getFirstMatch(
                ModRecipes.FOOD_DRYING_RECIPE_TYPE,
                recipeInput,
                world
            )
            .map { it.value }
            .orElse(null)
    }
    
    companion object {
        const val SLOT_COUNT = 4
        
        /**
         * Duration for drying a single food item in seconds.
         * Balanced for gameplay - long enough for meaningful progression,
         * short enough to maintain engagement.
         */
        const val DRYING_DURATION_SECS = 20
        const val DRYING_DURATION_TICKS = DRYING_DURATION_SECS * 20
    }

    /**
     * Checks if an item stack is valid input for food drying.
     */
    private fun isValidInput(stack: ItemStack): Boolean {
        if (world == null) return true // Allow during initialization
        return findRecipeForItem(stack, world!!) != null
    }

    /**
     * Gets the current progress for a specific slot as a percentage (0-100).
     */
    fun getSlotProgress(slot: Int): Int {
        if (slot < 0 || slot >= SLOT_COUNT) return 0
        return ((slotProgress[slot] / DRYING_DURATION_TICKS) * 100).toInt()
    }

    /**
     * Gets scaled progress for GUI rendering.
     */
    fun getScaledProgress(slot: Int, maxWidth: Int): Int {
        if (slot < 0 || slot >= SLOT_COUNT) return 0
        return (slotProgress[slot] * maxWidth / DRYING_DURATION_TICKS).toInt()
    }

    /**
     * Gets the item stack in a specific slot (for rendering).
     */
    fun getStackInSlot(slot: Int): ItemStack {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY
        return inventory.getStack(slot)
    }

    /**
     * Returns rendering data for items hanging on the dryer structure.
     * Used by FoodDryerBlockEntityRenderer for 3D item display.
     */
    fun getRenderingData(): List<ItemRenderData> {
        val data = mutableListOf<ItemRenderData>()
        
        for (slot in 0 until SLOT_COUNT) {
            val stack = inventory.getStack(slot)
            if (!stack.isEmpty) {
                data.add(ItemRenderData(
                    slot = slot,
                    stack = stack,
                    progress = slotProgress[slot] / DRYING_DURATION_TICKS
                ))
            }
        }
        
        return data
    }


    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }

    override fun getScreenOpeningData(player: ServerPlayerEntity): BlockPos {
        return this.pos
    }

    override fun getDisplayName(): Text {
        return Text.translatable("block.paleolithic-era.food_dryer")
    }

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler {
        return FoodDryerScreenHandler(syncId, playerInventory, pos)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = 
        BlockEntityUpdateS2CPacket.create(this)

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound = 
        createNbt(registries)

    /**
     * Reads the entity's data from NBT or component storage.
     *
     * Loads the food dryer's inventory contents and individual slot progress from persistent storage,
     * ensuring that drying operations are properly restored when the world is loaded.
     * This is essential for maintaining the player's food processing state across game sessions.
     *
     * @param view The data source to read from
     */
    override fun readData(view: net.minecraft.storage.ReadView) {
        super.readData(view)
        Inventories.readData(view, inventory.heldStacks)
        
        // Read the individual slot progress values
        for (slot in 0 until SLOT_COUNT) {
            slotProgress[slot] = view.getFloat("SlotProgress$slot", 0.0f)
        }
    }

    /**
     * Writes the entity's data to NBT or component storage.
     *
     * Saves the food dryer's inventory contents and individual slot progress to persistent storage,
     * ensuring that valuable food items and drying progress are not lost when the world is
     * unloaded. This is particularly important for the time-consuming food drying process.
     *
     * @param view The data destination to write to
     */
    override fun writeData(view: net.minecraft.storage.WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
        
        // Write the individual slot progress values
        for (slot in 0 until SLOT_COUNT) {
            view.putFloat("SlotProgress$slot", slotProgress[slot])
        }
    }


    /**
     * Data class for item rendering information.
     * Used by the block entity renderer to display hanging items.
     */
    data class ItemRenderData(
        val slot: Int,
        val stack: ItemStack,
        val progress: Float // 0.0 to 1.0
    )

}