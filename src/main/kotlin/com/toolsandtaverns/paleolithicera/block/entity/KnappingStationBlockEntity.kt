package com.toolsandtaverns.paleolithicera.block.entity

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
 * Block entity that stores inventory and handles logic for the Knapping Station.
 */
class KnappingStationBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModEntities.KNAPPING_STATION, pos, state),
    ExtendedScreenHandlerFactory<BlockPos> {

    private val INPUT_SLOT: Int = 0
    private val OUTPUT_SLOT: Int = 1

    private val inventory = SimpleInventory(2)
    private var knappingTicks = 0

    override fun readData(view: ReadView) {
        Inventories.readData(view, inventory.heldStacks)
        super.readData(view)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
    }

    fun getInventory(): SimpleInventory = inventory

    override fun getDisplayName(): Text? {
        return Text.translatable("block.paleolithic-era.knapping_station")
    }

    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity
    ): ScreenHandler? {
        return KnappingScreenHandler(syncId, playerInventory, pos)
    }

    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }


            /**
     * Attempts to start the knapping process.
     * This should be called from the block when the player sneak-uses the station.
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

    fun tick(world: World) {
        return
    }

    private fun craft() {
        val match: Optional<RecipeEntry<KnapRecipe>> = getMatchedRecipe()

        if (match.isPresent) {
            val output: ItemStack = match.get().value().output.copy()
            inventory.getStack(INPUT_SLOT).decrement(1)
            inventory.setStack(1, output)
        }
    }

    private fun getMatchedRecipe(): Optional<RecipeEntry<KnapRecipe>> {
        return (this.getWorld() as ServerWorld).recipeManager
            .getFirstMatch(
                ModRecipes.KNAPPING_RECIPE_TYPE,
                KnapRecipeInput(inventory.getStack(INPUT_SLOT)),
                this.getWorld()
            )
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound = createNbt(registries)

    override fun getScreenOpeningData(player: ServerPlayerEntity): BlockPos {
        return this.pos
    }
}
