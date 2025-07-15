package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.datagen.loot.MobLootModifier
import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import com.toolsandtaverns.paleolithicera.datagen.loot.PlantFiberLootModifier
import com.toolsandtaverns.paleolithicera.registry.ModCriteria
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PaleolithicEra : ModInitializer {

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initializing Paleolithic Era")
        ModBlocks.initialize()
        ModItems.initialize()
        ModBlockEntities.initialize()
        ModScreenHandlers.initialize()
        ModRecipes.initialize()
        ModCriteria.initialize()
        PlantFiberLootModifier.initialize()
        MobLootModifier.initialize()
    }
}