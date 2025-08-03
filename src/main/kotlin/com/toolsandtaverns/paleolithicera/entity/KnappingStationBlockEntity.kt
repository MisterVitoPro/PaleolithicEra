package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.recipe.KnapRecipe
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipeInput
import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import com.toolsandtaverns.paleolithicera.screen.KnappingScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
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
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Block entity that implements the knapping mechanic - a core Paleolithic tool-making process.
 * 
 * The Knapping Station represents one of the earliest tool creation methods used by humans,
 * where stones are carefully struck against each other to create sharp edges and specific shapes.
 * In gameplay terms, this station allows players to transform raw materials (like flint) into
 * more useful tool components and primitive implements.
 * 
 * This block entity manages:
 * - A two-slot inventory (input materials and output products)
 * - The knapping process timing and progression
 * - Recipe lookup and crafting logic specific to knapped items
 * - Interaction with players through a dedicated GUI
 * 
 * The knapping process requires time and player interaction, reflecting the skill and patience
 * required for early tool-making technologies.
 */
class KnappingStationBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModEntities.KNAPPING_STATION, pos, state),
    ExtendedScreenHandlerFactory<BlockPos> {

    /**
     * Index of the input slot in the inventory where raw materials are placed.
     * Materials like flint, obsidian, or other knappable items go here.
     */
    private val INPUT_SLOT: Int = 0

    /**
     * Index of the output slot in the inventory where crafted items appear.
     * Knapped products like bifaces, knife blades, or arrowheads are placed here.
     */
    private val OUTPUT_SLOT: Int = 1

    /**
     * Simple two-slot inventory for the knapping station.
     * The limited inventory size reflects the primitive nature of the knapping process,
     * focusing on transforming a single resource rather than complex multi-material crafting.
     */
    private val inventory = SimpleInventory(2)

    /**
     * Counter that tracks the progress of the current knapping operation in ticks.
     * Knapping is a time-consuming process that requires player patience, reflecting the
     * historical reality of stone tool creation in the Paleolithic era.
     */
    private var knappingTicks = 0

    /**
     * Reads the entity's data from NBT or component storage.
     * 
     * Loads the knapping station's inventory contents from persistent storage,
     * ensuring that in-progress crafting operations are properly restored when
     * the world is loaded. This is essential for maintaining the player's crafting
     * state across game sessions.
     *
     * @param view The data source to read from
     */
    override fun readData(view: ReadView) {
        Inventories.readData(view, inventory.heldStacks)
        super.readData(view)
    }

    /**
     * Writes the entity's data to NBT or component storage.
     * 
     * Saves the knapping station's inventory contents to persistent storage,
     * ensuring that valuable raw materials and crafting progress are not lost
     * when the world is unloaded. This is particularly important for Paleolithic
     * gameplay where resources like flint can be scarce and time-consuming to gather.
     *
     * @param view The data destination to write to
     */
    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
    }

    /**
     * Provides access to the knapping station's inventory.
     * 
     * This method allows other game systems (like screens and containers) to interact
     * with the station's inventory, enabling item insertion, extraction, and display.
     * The inventory represents the physical workspace where raw stone is transformed
     * into tools through the knapping process.
     *
     * @return The inventory containing input and output slots
     */
    fun getInventory(): SimpleInventory = inventory

    /**
     * Returns the display name of the knapping station for the UI.
     * 
     * This localized text appears in the GUI when players interact with the station,
     * providing clear identification of the specialized crafting interface they're using.
     * The name "Knapping Station" directly communicates the Paleolithic technology theme.
     *
     * @return Localized text for the knapping station title
     */
    override fun getDisplayName(): Text? {
        return Text.translatable("block.paleolithic-era.knapping_station")
    }

    /**
     * Creates a screen handler for player interaction with the knapping station.
     * 
     * This method connects the block entity to the screen system, allowing players to
     * visually interact with the knapping process through a GUI. The specialized interface
     * reflects the unique nature of stone tool crafting in the Paleolithic era.
     *
     * @param syncId Synchronization ID for the container
     * @param playerInventory The player's inventory
     * @param player The player interacting with the station
     * @return A screen handler for the knapping station UI
     */
    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler? {
        return KnappingScreenHandler(syncId, playerInventory, pos)
    }

    /**
     * Handles behavior when the knapping station block is replaced or destroyed.
     * 
     * Drops any items contained in the station's inventory into the world when the block
     * is broken, ensuring players don't lose valuable resources like flint or partially
     * crafted tools. This maintains the resource-conscious gameplay balance of the
     * Paleolithic era mod, where materials are precious.
     *
     * @param pos The position of the block
     * @param oldState The previous blockstate
     */
    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }


    /**
     * Attempts to progress the knapping process when a player interacts with the station.
     * 
     * This method simulates the repetitive striking actions used in stone knapping, where
     * a toolmaker would repeatedly strike a core stone to shape it into a useful implement.
     * Each interaction represents a knapping strike, with multiple strikes needed to complete
     * a tool.
     * 
     * The method:
     * 1. Checks if valid input exists and output slot is empty
     * 2. Increments the knapping progress counter
     * 3. Completes the craft once sufficient progress is made (20 ticks)
     * 4. Resets the counter and updates the world state
     * 
     * This direct player interaction requirement reflects the hands-on nature of Paleolithic
     * crafting techniques, where tool creation was a skilled, manual process.
     *
     * @param world The world containing the knapping station
     */
    fun knap(world: World) {
        val input = inventory.getStack(INPUT_SLOT)
        val output = inventory.getStack(OUTPUT_SLOT)

        if (!input.isEmpty && output.isEmpty) {
            knappingTicks++
            if (world !is ServerWorld) return

            if (knappingTicks >= 20) {
                println("Before - Tick() - Inventory Input (0): ${inventory.heldStacks[INPUT_SLOT]}")
                println("Before - Tick() - Inventory Output (1): ${inventory.heldStacks[OUTPUT_SLOT]}")
                if (!inventory.getStack(INPUT_SLOT).isEmpty) {
                    craft()
                }
                knappingTicks = 0
                markDirty()
                world.chunkManager.markForUpdate(pos)
                println("Tick() - Inventory Input (0): ${inventory.heldStacks[INPUT_SLOT]}")
                println("Tick() - Inventory Output (1): ${inventory.heldStacks[OUTPUT_SLOT]}")
            }
        }
    }

    /**
     * Handles the automatic tick update for the knapping station.
     * 
     * This method is intentionally empty as knapping is designed to be a manual process
     * requiring direct player interaction rather than automatic progression. This design
     * choice reinforces the hands-on nature of Paleolithic crafting, where tools were
     * created through physical skill and effort rather than automated processes.
     *
     * @param world The world containing the knapping station
     */
    fun tick(world: World) {
        return
    }

    /**
     * Performs the actual crafting operation once sufficient knapping has occurred.
     * 
     * This method transforms the input material into a crafted tool component by:
     * 1. Finding a matching knapping recipe for the input material
     * 2. Creating a copy of the output defined by that recipe
     * 3. Consuming one item from the input stack
     * 4. Placing the created item in the output slot
     * 
     * The transformation represents the skilled process of stone knapping, where raw materials
     * like flint are shaped into useful implements through controlled fracturing.
     */
    private fun craft() {
        val match: Optional<RecipeEntry<KnapRecipe>> = getMatchedRecipe()

        if (match.isPresent) {
            val output: ItemStack = match.get().value().output.copy()
            inventory.getStack(INPUT_SLOT).decrement(1)
            inventory.setStack(1, output)
        }
    }

    /**
     * Finds a matching knapping recipe for the current input item.
     * 
     * This method queries the recipe system to determine what can be crafted from
     * the material in the input slot. The specialized KnapRecipe type represents
     * stone tool crafting processes unique to the Paleolithic era, with different
     * raw materials producing different tool components.
     * 
     * This recipe system allows for expansion of knappable materials and craftable
     * tools as the mod evolves, supporting the technological progression theme.
     *
     * @return An Optional containing the matching recipe if found
     */
    private fun getMatchedRecipe(): Optional<RecipeEntry<KnapRecipe>> {
        return (this.getWorld() as ServerWorld).recipeManager
            .getFirstMatch(
                ModRecipes.KNAPPING_RECIPE_TYPE,
                KnapRecipeInput(inventory.getStack(INPUT_SLOT)),
                this.getWorld()
            )
    }

    /**
     * Creates a packet to send this block entity's data to clients.
     * 
     * This network synchronization ensures that all players see the same state of the
     * knapping station, including its inventory contents. This is crucial for multiplayer
     * scenarios where multiple players might be using or observing the same crafting station.
     *
     * @return A packet containing this entity's data for client synchronization
     */
    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    /**
     * Creates NBT data to send when a chunk is first loaded by a client.
     * 
     * This provides the initial state of the knapping station when a player first encounters it,
     * including any materials or partially completed crafting operations. This is important for
     * maintaining continuity in shared crafting spaces in multiplayer environments.
     *
     * @param registries Registry wrapper for data serialization
     * @return NBT compound containing initial chunk data
     */
    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound = createNbt(registries)

    /**
     * Provides data needed when opening the knapping station screen.
     * 
     * This method is part of the ExtendedScreenHandlerFactory interface and returns
     * the position of this block entity. This position is used by the screen handler
     * to locate and interact with the correct block entity in the world.
     *
     * @param player The server player opening the screen
     * @return The position of this knapping station
     */
    override fun getScreenOpeningData(player: ServerPlayerEntity): BlockPos {
        return this.pos
    }
}
