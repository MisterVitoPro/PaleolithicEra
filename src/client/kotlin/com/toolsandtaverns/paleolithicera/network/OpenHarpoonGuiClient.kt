package com.toolsandtaverns.paleolithicera.network

import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import com.toolsandtaverns.paleolithicera.network.payload.HarpoonResultPayload
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
     * Called when the player completes the fishing minigame (by pressing space),
     * sending whether they succeeded or failed to the server for reward processing.
     * 
     * @param success Whether the player successfully caught a fish
     */
    fun sendResult(success: Boolean) {
        // Send a packet to the server containing the success/failure status
        ClientPlayNetworking.send(HarpoonResultPayload(success))
    }

    /**
     * Registers client-side network packet handlers for harpoon fishing.
     * 
     * Sets up a receiver for server-to-client packets that tell the client
     * to open the harpoon fishing minigame screen.
     */
    fun register() {
        // Register a handler for the server packet that opens the fishing GUI
        ClientPlayNetworking.registerGlobalReceiver(OpenHarpoonGuiPayload.getId()) { _, _ ->
            // Execute on the main client thread for thread safety
            MinecraftClient.getInstance().execute {
                // Open the harpoon fishing minigame screen
                MinecraftClient.getInstance().setScreen(HarpoonFishingScreen())
            }
        }
    }
}
