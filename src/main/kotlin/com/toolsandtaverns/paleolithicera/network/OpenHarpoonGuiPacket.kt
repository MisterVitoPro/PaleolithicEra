package com.toolsandtaverns.paleolithicera.network

import com.toolsandtaverns.paleolithicera.network.payload.HarpoonResultPayload
import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import com.toolsandtaverns.paleolithicera.item.HarpoonItem
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Server-side packet handler for harpoon fishing functionality.
 *
 * Handles incoming client packets related to the harpoon fishing minigame,
 * processes results, and provides appropriate rewards.
 */
object OpenHarpoonGuiPacket {
    private const val START_DELAY_TICKS = 10L
    private const val ATTEMPT_TIMEOUT_TICKS = 20L * 60L

    private data class PendingAttempt(
        val id: Long,
        val item: Item,
        val hand: Hand,
        val startTick: Long,
        val expiresAt: Long,
        val targetStartStep: Int
    )

    private val pendingAttempts = ConcurrentHashMap<UUID, PendingAttempt>()

    fun beginAttempt(player: ServerPlayerEntity, item: Item, hand: Hand): OpenHarpoonGuiPayload {
        val startTick = player.world.time + START_DELAY_TICKS
        val targetStartStep = Random.nextInt(
            0,
            HarpoonMinigameRules.SLIDER_MAX_STEP - HarpoonMinigameRules.TARGET_WIDTH_STEPS + 1
        )
        val attempt = PendingAttempt(
            id = Random.nextLong(),
            item = item,
            hand = hand,
            startTick = startTick,
            expiresAt = startTick + ATTEMPT_TIMEOUT_TICKS,
            targetStartStep = targetStartStep
        )
        pendingAttempts[player.uuid] = attempt
        return OpenHarpoonGuiPayload(attempt.id, attempt.startTick, attempt.targetStartStep)
    }

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
            handleResult(player, payload.attemptId)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            pendingAttempts.remove(handler.player.uuid)
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
     * @param attemptId The server-issued identifier for the completed attempt
     */
    private fun handleResult(player: ServerPlayerEntity, attemptId: Long) {
        val attempt = pendingAttempts[player.uuid] ?: return
        if (attempt.id != attemptId || !pendingAttempts.remove(player.uuid, attempt)) return

        val stack: ItemStack = player.getStackInHand(attempt.hand)
        val item: Item = stack.item
        if (player.world.time > attempt.expiresAt || item !is HarpoonItem || item !== attempt.item) return
        val slot = ServerPlayerEntity.getSlotForHand(attempt.hand)
        val success = HarpoonMinigameRules.isSuccessful(
            attempt.targetStartStep,
            attempt.startTick,
            player.world.time
        )

        // Damage the harpoon and handle potential breakage
        //? if >=1.21.6 {
        stack.damage(1, player, attempt.hand)
        //?} else {
        /*stack.damage(1, player, slot)*///?}
        if (stack.isEmpty) {
            // Notify the client that the item broke for proper visual/sound effects
            player.sendEquipmentBreakStatus(item, slot)
        }
        
        // Update the stack in the player's inventory to ensure changes are synced
        player.setStackInHand(attempt.hand, stack)

        // Handle success case - give rewards if applicable
        if (success) {
            val reward: Item? = getFishReward(item)
            if (reward != null) {
                // Give the player the caught fish
                player.giveItemStack(ItemStack(reward))
                // Display a message with the name of the caught fish
                val message = Text.translatable("message.paleolithic-era.caught_item", reward.defaultStack.name)
                player.sendMessage(message, true)
            } else {
                // The player succeeded but the fish still escaped (bad luck)
                player.sendMessage(Text.translatable("message.paleolithic-era.fish_escaped"), true)
            }
        } else {
            player.sendMessage(Text.translatable("message.paleolithic-era.fish_miss"), true)
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
    private fun getFishReward(item: Item): Item? {
        // Improved tier gets better odds
        val rand = Random.nextFloat()
        return if (ModItems.BONE_HARPOON == item) {
            when {
                rand < 0.45f -> Items.COD          // 45%
                rand < 0.65f -> Items.SALMON       // 20%
                rand < 0.70f -> Items.TROPICAL_FISH // 5%
                else -> null                       // 30%
            }
        } else {
            when {
                rand < 0.48f -> Items.COD       // 40% chance
                else -> null  // 52% chance of no fish (fish escaped)
            }
        }
    }
}
