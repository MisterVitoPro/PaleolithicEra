package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.events.TooltipEvents
import com.toolsandtaverns.paleolithicera.render.KnappingStationBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.model.BoarModel
import com.toolsandtaverns.paleolithicera.model.WoodenSpearProjectileModel
import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiClient
import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import com.toolsandtaverns.paleolithicera.render.BoarRenderer
import com.toolsandtaverns.paleolithicera.render.CrudeCampfireBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.render.WoodenSpearRenderer
import com.toolsandtaverns.paleolithicera.screen.HideDryerScreen
import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.BlockRenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

/**
     * Client-side initialization for the Paleolithic Era mod.
     * 
     * This class handles all client-specific initialization such as registering
     * renderers, screens, and client-side network handlers.
     */
    object PaleolithicEraClient : ClientModInitializer {
    /**
     * Initializes the client-side components of the mod.
     * 
     * This method registers:
     * - Network packet handlers for client-server communication
     * - Block entity renderers for custom blocks
     * - Entity renderers for custom entities
     * - GUI screens for custom containers
     * - Block render layers for blocks with transparency
     */
    override fun onInitializeClient() {
        // Register the network packet type for opening the harpoon GUI
        PayloadTypeRegistry.playS2C().register(
            OpenHarpoonGuiPayload.id,
            OpenHarpoonGuiPayload.TYPE.codec()
        )

        // Register the custom renderer for the crude campfire block entity
        // This renderer displays cooking items above the campfire
        BlockEntityRendererFactories.register(ModEntities.CRUDE_CAMPFIRE, ::CrudeCampfireBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModEntities.KNAPPING_STATION, ::KnappingStationBlockEntityRenderer)

        // Register the renderer for the wooden spear entity
        // This allows thrown spears to be properly displayed in the world
        EntityModelLayerRegistry.registerModelLayer(BoarModel.BOAR_MODEL_LAYER, BoarModel::texturedModelData)
        EntityModelLayerRegistry.registerModelLayer(WoodenSpearProjectileModel.WOOD_SPEAR_MODEL_LAYER, WoodenSpearProjectileModel::texturedModelData)

        EntityRendererRegistry.register(ModEntities.BOAR_ENTITY) { context -> BoarRenderer(context) }
        EntityRendererRegistry.register(ModEntities.WOODEN_SPEAR_ENTITY) { context ->
            WoodenSpearRenderer(context)
        }

        // Register the screen for the knapping station
        // This connects the container handler to its GUI implementation

        HandledScreens.register(ModScreenHandlers.KNAPPING, ::KnappingStationScreen)
        HandledScreens.register(ModScreenHandlers.HIDE_DRYER, ::HideDryerScreen)

        // Set the render layers for blocks with transparency
        // CUTOUT is used for blocks with binary transparency (fully transparent or fully opaque pixels)
        BlockRenderLayerMap.putBlock(ModBlocks.CRUDE_CAMPFIRE, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.ELDERBERRY_BUSH, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.YARROW_PLANT, BlockRenderLayer.CUTOUT)

        // Register client-side network handlers for the harpoon fishing system
        OpenHarpoonGuiClient.register()
        TooltipEvents.register()
    }
}
