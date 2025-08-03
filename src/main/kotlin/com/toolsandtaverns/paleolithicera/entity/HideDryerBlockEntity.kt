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
 * HideDryerBlockEntity implements the hide drying process - a crucial step in early leather production.
 * 
 * The Hide Dryer represents an essential Paleolithic technology for transforming raw animal hides
 * into usable leather materials. This process simulates how early humans would stretch and dry hides
 * in the sun to prepare them for clothing, containers, and shelter materials.
 * 
 * Key gameplay features:
 * - Operates only during daylight hours (simulating sun-drying)
 * - Automatically processes rawhide into dry hide over time
 * - Provides a dedicated interface for monitoring and managing the drying process
 * - Creates a time-gate for progression in leather-based crafting technologies
 * 
 * This block entity serves as an important early-game crafting station that bridges
 * hunting activities with clothing and equipment crafting.
 */
class HideDryerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModEntities.HIDE_DRYER_BLOCK_ENTITY, pos, state), ExtendedScreenHandlerFactory<BlockPos> {

    /**
     * Handles the core drying process logic during world ticks.
     * 
     * This method simulates the natural sun-drying of animal hides by:
     * 1. Checking if it's daytime (hides only dry during the day in sunlight)
     * 2. Verifying input and output slots contain appropriate items
     * 3. Incrementing progress until the required drying time is reached
     * 4. Transforming rawhide into dry hide when complete
     * 
     * The daylight requirement creates a realistic time constraint that forces players
     * to plan their hide processing around the day/night cycle, similar to how Paleolithic
     * humans would have been constrained by natural cycles for their crafting activities.
     *
     * @param world The world containing this block entity
     */
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

    /**
     * Tracks the current progress of the drying operation in ticks.
     * When this reaches DRYING_DURATION_TICKS, the rawhide is transformed into dry hide.
     * This slower processing reflects the time-consuming nature of hide preparation in the Paleolithic era.
     */
    private var progress = 0

    /**
     * Simple two-slot inventory for the hide dryer:
     * - Slot 0: Input slot for rawhide items
     * - Slot 1: Output slot where dried hide appears
     * 
     * This minimal inventory reflects the straightforward nature of the drying process,
     * which transforms a single input resource into a processed version without additional ingredients.
     */
    val inventory = SimpleInventory(2)

    /**
     * Property delegate that exposes the drying progress to the screen handler system.
     * 
     * This delegate allows the GUI to display a visual progress indicator that shows
     * the current status of the drying process. This visual feedback is important for
     * players to understand how long they need to wait for their hides to process.
     * 
     * Currently exposes:
     * - Index 0: Current drying progress (in ticks)
     * 
     * The visual representation of time-based crafting reinforces the patient, methodical
     * nature of Paleolithic technologies, where processes couldn't be rushed and required
     * waiting for natural processes to complete.
     */
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

    /**
     * Handles behavior when the hide dryer block is replaced or destroyed.
     * 
     * Drops any items contained in the dryer's inventory into the world when the block
     * is broken, ensuring players don't lose valuable rawhide or dried hides. This is
     * particularly important since rawhide represents hunting effort and time investment.
     *
     * @param pos The position of the block
     * @param oldState The previous blockstate
     */
    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }

    /**
     * Provides data needed when opening the hide dryer screen.
     * 
     * This method is part of the ExtendedScreenHandlerFactory interface and returns
     * the position of this block entity, which is used to locate and interact with
     * the correct dryer in the world when the GUI is open.
     *
     * @param player The server player opening the screen
     * @return The position of this hide dryer
     */
    override fun getScreenOpeningData(player: ServerPlayerEntity): BlockPos {
        return this.pos
    }

    /**
     * Returns the display name of the hide dryer for the UI.
     * 
     * This localized text appears in the GUI when players interact with the dryer,
     * clearly identifying the specialized function of this crafting station in the
     * leather production chain.
     *
     * @return Localized text for the hide dryer title
     */
    override fun getDisplayName(): Text? {
        return Text.translatable("block.paleolithic-era.hide_dryer")
    }

    /**
     * Creates a screen handler for player interaction with the hide dryer.
     * 
     * This method connects the block entity to the screen system, allowing players to
     * visually interact with the drying process through a dedicated GUI. The interface
     * shows input and output slots along with a progress indicator for the drying process.
     *
     * @param syncId Synchronization ID for the container
     * @param playerInventory The player's inventory
     * @param player The player interacting with the dryer
     * @return A screen handler for the hide dryer UI
     */
    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler? {
        return HideDryerScreenHandler(syncId, playerInventory, pos)
    }

    /**
     * Constants that define the hide drying process parameters.
     * 
     * These values determine how long the drying process takes, balancing gameplay
     * pacing with realistic simulation of the time-consuming nature of primitive
     * hide processing techniques.
     */
    companion object {
        /**
         * The duration required to dry a single rawhide in seconds.
         * Set to 15 seconds for gameplay balance - long enough to create meaningful
         * time investment but short enough to maintain player engagement.
         */
        const val DRYING_DURATION_SECS = 15

        /**
         * The duration required to dry a single rawhide in game ticks.
         * (20 ticks = 1 second in standard Minecraft time)
         */
        const val DRYING_DURATION_TICKS = DRYING_DURATION_SECS * 20
    }

    /**
     * Creates a packet to send this block entity's data to clients.
     * 
     * This network synchronization ensures that all players see the same state of the
     * hide dryer, including its inventory contents and drying progress. This is particularly
     * important in multiplayer settings where multiple players might interact with the same dryer.
     *
     * @return A packet containing this entity's data for client synchronization
     */
    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    /**
     * Creates NBT data to send when a chunk is first loaded by a client.
     * 
     * This provides the initial state of the hide dryer when a player first encounters it,
     * including any materials already in the drying process. This ensures visual consistency
     * when players approach an existing dryer in the world.
     *
     * @param registries Registry wrapper for data serialization
     * @return NBT compound containing initial chunk data
     */
    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound = createNbt(registries)
}
