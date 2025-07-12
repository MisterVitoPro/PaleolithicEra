package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.registry.ModRecipes
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object PaleolithicEra : ModInitializer {

    val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Initializing Paleolithic Era")
		ModBlocks.initialize()
		ModItems.initialize()
		ModBlockEntities.initialize()
		ModScreenHandlers.initialize()
		ModRecipes.initialize()
	}
}