package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.datagen.loot.MobLootModifier
import com.toolsandtaverns.paleolithicera.datagen.loot.PlantFiberLootModifier
import com.toolsandtaverns.paleolithicera.datagen.loot.RockChunkLootModifier
import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiPacket
import com.toolsandtaverns.paleolithicera.network.payload.HarpoonResultPayload
import com.toolsandtaverns.paleolithicera.registry.*
import com.toolsandtaverns.paleolithicera.world.gen.ModWorldgen
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

    /**
     * Main mod class for the Paleolithic Era mod.
     * 
     * This class serves as the entry point for the mod and handles initialization
     * of all mod components, registries, and network handlers.
     */
    object PaleolithicEra : ModInitializer {

    /** Logger instance for mod-related logging */
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    /**
     * Initializes the mod when Minecraft starts up.
     * 
     * This method registers all mod components including:
     * - Custom blocks and items
     * - Entity types
     * - Screen handlers (GUIs)
     * - Recipes and advancements
     * - Loot table modifiers
     * - World generation features
     * - Network packets
     */
    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initializing Paleolithic Era")

        // Register the packet codec for client-to-server harpoon result communication
        PayloadTypeRegistry.playC2S().register(HarpoonResultPayload.ID, HarpoonResultPayload.CODEC)

        // Initialize block registry with custom blocks
        ModBlocks.initialize()
        // Initialize item registry with custom items
        ModItems.initialize()
        // Initialize entity types (including block entities)
        ModEntities.initialize()
        // Initialize container/GUI screen handlers
        ModScreenHandlers.initialize()
        // Initialize custom crafting recipes
        ModRecipes.initialize()
        // Initialize advancement criteria
        ModCriteria.initialize()

        // Initialize loot table modifiers for custom drops
        PlantFiberLootModifier.initialize() // Adds plant fiber drops to grass
        MobLootModifier.initialize()        // Adds custom mob drops
        RockChunkLootModifier.initialize()  // Adds rock chunk drops to stone

        // Initialize custom world generation features
        ModWorldgen.initialize()

        // Register packet handlers for network communication
        OpenHarpoonGuiPacket.register()
    }

}