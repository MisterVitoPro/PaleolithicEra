package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.entity.BoarEntity
import com.toolsandtaverns.paleolithicera.event.BlockDropHandler
import com.toolsandtaverns.paleolithicera.event.MobLootModifier
import com.toolsandtaverns.paleolithicera.event.PlantFiberLootModifier
import com.toolsandtaverns.paleolithicera.event.RockChunkLootModifier
import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiPacket
import com.toolsandtaverns.paleolithicera.network.payload.HarpoonResultPayload
import com.toolsandtaverns.paleolithicera.registry.*
import com.toolsandtaverns.paleolithicera.world.gen.ModTreeGeneration
import com.toolsandtaverns.paleolithicera.world.gen.ModWorldgen
import com.toolsandtaverns.paleolithicera.world.gen.treedecorator.ModTreeDecoratorType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
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
        LOGGER.info("Initializing Paleolithic Era")

        // Register the packet codec for client-to-server harpoon result communication
        PayloadTypeRegistry.playC2S().register(HarpoonResultPayload.ID, HarpoonResultPayload.CODEC)

        // Initialize item registry with custom items
        ModItems.initialize()
        // Initialize block registry with custom blocks
        ModBlocks.initialize()
        // Initialize entity types (including block entities)
        ModEntityType.initialize()
        // Initialize container/GUI screen handlers
        ModScreenHandlers.initialize()
        // Initialize custom crafting recipes
        ModRecipes.initialize()
        // Initialize advancement criteria
        ModCriteria.initialize()
        ModItemGroups.register()
        ModTreeDecoratorType.initialize()

        FabricDefaultAttributeRegistry.register(ModEntityType.BOAR_ENTITY, BoarEntity.createAttributes())

        // Initialize loot table modifiers for custom drops
        PlantFiberLootModifier.initialize() // Adds plant fiber drops to grass
        MobLootModifier.initialize()        // Adds custom mob drops
        RockChunkLootModifier.initialize()  // Adds rock chunk drops to stone

        // Register packet handlers for network communication
        OpenHarpoonGuiPacket.register()

        BlockDropHandler.register()

        // Initialize custom world generation features
        ModWorldgen.initialize()
        ModTreeGeneration.initialize()

        ModFuelRegistry.registerFuels()

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_LOG, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_WILLOW_LOG, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_LEAF_VINES, 5, 5)

        StrippableBlockRegistry.register(ModBlocks.WILLOW_LOG, ModBlocks.STRIPPED_WILLOW_LOG)
    }

}