package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.entity.BoarEntity
import com.toolsandtaverns.paleolithicera.entity.IbexEntity
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
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.minecraft.block.CampfireBlock
import net.minecraft.item.FireChargeItem
import net.minecraft.item.FlintAndSteelItem
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.toolsandtaverns.paleolithicera.event.SpawnGate
import com.toolsandtaverns.paleolithicera.progression.WorldProgress
import com.toolsandtaverns.paleolithicera.progression.ProgressionRules
import net.minecraft.server.world.ServerWorld
import net.minecraft.item.Items
import net.minecraft.item.SpawnEggItem

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
        // Initialize advancement criteria
        ModCriteria.initialize()
        ModItemGroups.register()
        ModTreeDecoratorType.initialize()

        FabricDefaultAttributeRegistry.register(ModEntityType.BOAR_ENTITY, BoarEntity.createAttributes())
        FabricDefaultAttributeRegistry.register(ModEntityType.IBEX_ENTITY, IbexEntity.createAttributes())

        // Initialize loot table modifiers for custom drops
        PlantFiberLootModifier.initialize() // Adds plant fiber drops to grass
        MobLootModifier.initialize()        // Adds custom mob drops
        RockChunkLootModifier.initialize()  // Adds rock chunk drops to stone

        // Register packet handlers for network communication
        OpenHarpoonGuiPacket.register()

        BlockDropHandler.register()

        // Initialize custom world generation features
        ModTreeGeneration.initialize()
        ModWorldgen.initialize()

        ModFuelRegistry.registerFuels()

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_LOG, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_WILLOW_LOG, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_LEAF_VINES, 5, 5)

        StrippableBlockRegistry.register(ModBlocks.WILLOW_LOG, ModBlocks.STRIPPED_WILLOW_LOG)

        // Initialize custom crafting recipes
        ModRecipes.initialize()

        // Ensure custom gamerules are registered before any world access
        // Touch keys to force ProgressionRules object init
        val campfireRuleKey = ProgressionRules.CAMPFIRE_LIT
        val knapRuleKey = ProgressionRules.KNAPPING_STATION_PLACED

        // Detect vanilla campfire lighting (e.g., flint and steel)
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            if (!world.isClient) {
                val pos = hitResult.blockPos
                val state = world.getBlockState(pos)
                val item = player.getStackInHand(hand).item
                if (state.block is CampfireBlock && !state.get(Properties.LIT)) {
                    if (item is FlintAndSteelItem || item is FireChargeItem) {
                        WorldProgress.markCampfireLit(world as ServerWorld)
                    }
                }
                // Track spawn egg usage for pig/goat so we can allow those spawns
                if (world is ServerWorld) {
                    when (item) {
                        Items.PIG_SPAWN_EGG -> SpawnGate.noteSpawnEggUse(world, pos, net.minecraft.entity.EntityType.PIG)
                        Items.GOAT_SPAWN_EGG -> SpawnGate.noteSpawnEggUse(world, pos, net.minecraft.entity.EntityType.GOAT)
                    }
                }
            }
            ActionResult.PASS
        })

        // Effigy placed advancement is triggered at structure conversion time

        // Gate pig/goat spawns until progression is met
        SpawnGate.initialize()
    }

}
