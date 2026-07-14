package com.toolsandtaverns.paleolithicera.network

import com.toolsandtaverns.paleolithicera.network.payload.HarpoonResultPayload
import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import com.toolsandtaverns.paleolithicera.screen.HarpoonFishingScreen
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient

/**
 * Client-side network handler for the harpoon fishing minigame.
 *
 * This object handles both receiving packets from the server to open the fishing GUI
 * and sending results back to the server when the player completes the minigame.
 */
object OpenHarpoonGuiClient {

    /**
     * Sends the result of a fishing attempt to the server.
     *
     * Called when the player strikes in the fishing minigame. The server determines
     * success from its own attempt parameters and receipt time.
     *
     * @param attemptId The identifier issued by the server for this attempt
     */
    fun sendResult(attemptId: Long) {
        ClientPlayNetworking.send(HarpoonResultPayload(attemptId))
    }

    /**
     * Registers client-side network packet handlers for harpoon fishing.
     *
     * Sets up a receiver for server-to-client packets that tell the client
     * to open the harpoon fishing minigame screen.
     */
    fun register() {
        // Register a handler for the server packet that opens the fishing GUI
        ClientPlayNetworking.registerGlobalReceiver(OpenHarpoonGuiPayload.ID) { payload, _ ->
            // Execute on the main client thread for thread safety
            MinecraftClient.getInstance().execute {
                // Open the harpoon fishing minigame screen
                MinecraftClient.getInstance().setScreen(HarpoonFishingScreen(payload))
            }
        }
    }
}
