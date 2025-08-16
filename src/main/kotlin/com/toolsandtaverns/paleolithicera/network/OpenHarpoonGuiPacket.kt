package com.toolsandtaverns.paleolithicera.network

import com.toolsandtaverns.paleolithicera.network.payload.HarpoonResultPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import kotlin.random.Random

/**
 * Server-side packet handler for harpoon fishing functionality.
 *
 * Handles incoming client packets related to the harpoon fishing minigame,
 * processes results, and provides appropriate rewards.
 */
object OpenHarpoonGuiPacket {
    /**
     * Registers network packet receivers for harpoon fishing.
     *
     * Sets up handlers for client-to-server packets that are sent when
     * the player completes the fishing minigame.
     */
    fun register() {
        // Register a receiver for the harpoon result payload sent from client
        ServerPlayNetworking.registerGlobalReceiver(HarpoonResultPayload.ID) { payload, context ->
            val player = context.player()
            handleResult(player, payload.success)
        }
    }

    /**
     * Processes the result of a harpoon fishing attempt.
     *
     * This method:
     * 1. Damages the harpoon item by 1 point
     * 2. Shows a break animation if the item breaks
     * 3. If successful, gives the player a fish reward and displays a message
     * 4. If unsuccessful or if the fish escaped, displays an appropriate message
     *
     * @param player The player who attempted fishing
     * @param success Whether the player successfully caught a fish
     */
    private fun handleResult(player: ServerPlayerEntity, success: Boolean) {
        val stack = player.getStackInHand(Hand.MAIN_HAND)
        val item = player.getStackInHand(Hand.MAIN_HAND).item
        val slot = ServerPlayerEntity.getSlotForHand(Hand.MAIN_HAND)

        // Damage the harpoon and handle potential breakage
        stack.damage(1, player)
        if (stack.isEmpty) {
            // Notify the client that the item broke for proper visual/sound effects
            player.sendEquipmentBreakStatus(item, slot)
        }

        // Handle success case - give rewards if applicable
        if (success) {
            val reward: Item? = getFishReward()
            if (reward != null) {
                // Give the player the caught fish
                player.giveItemStack(ItemStack(reward))
                // Display a message with the name of the caught fish
                val message = Text.translatable("message.paleolithic-era.caught_item", reward.defaultStack.name)
                player.sendMessage(message, true)
            } else {
                // The player succeeded but the fish still escaped (bad luck)
                player.sendMessage(Text.literal("The fish escaped..."), true)
            }
        }
    }

    /**
     * Determines the fish reward for a successful harpoon fishing attempt.
     *
     * Uses a random number generator to select from different types of fish with
     * different probabilities:
     * - 30% chance for Cod
     * - 15% chance for Salmon
     * - 3% chance for Tropical Fish
     * - 52% chance of no fish (escape)
     *
     * @return The fish item to reward, or null if the fish escaped
     */
    private fun getFishReward(): Item? {
        val rand = Random.nextFloat()
        return when {
            rand < 0.30f -> Items.COD       // 30% chance
            rand < 0.45f -> Items.SALMON    // 15% chance (0.45 - 0.30)
            rand < 0.48f -> Items.TROPICAL_FISH  // 3% chance (0.48 - 0.45)
            else -> null  // 52% chance of no fish (fish escaped)
        }
    }
}
