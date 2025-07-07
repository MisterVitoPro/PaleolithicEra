package com.tootsandtaverns.paleolithicera

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import com.tootsandtaverns.paleolithicera.registry.ModBlockEntities
import com.tootsandtaverns.paleolithicera.registry.ModBlocks
import com.tootsandtaverns.paleolithicera.registry.ModItems
import com.tootsandtaverns.paleolithicera.registry.ModRecipes
import com.tootsandtaverns.paleolithicera.registry.ModScreenHandlers
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