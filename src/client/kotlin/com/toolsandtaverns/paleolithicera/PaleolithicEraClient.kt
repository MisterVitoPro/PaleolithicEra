package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.client.render.IbexRenderer
import com.toolsandtaverns.paleolithicera.entity.IbexEntity
import com.toolsandtaverns.paleolithicera.events.ToolTipEvents
import com.toolsandtaverns.paleolithicera.model.BoarModel
import com.toolsandtaverns.paleolithicera.model.BoneSpearProjectileModel
import com.toolsandtaverns.paleolithicera.model.IbexModel
import com.toolsandtaverns.paleolithicera.model.WoodenSpearProjectileModel
import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiClient
import com.toolsandtaverns.paleolithicera.network.payload.OpenHarpoonGuiPayload
import com.toolsandtaverns.paleolithicera.particle.PebbleImpactParticle
import com.toolsandtaverns.paleolithicera.particle.StoneDustParticle
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModParticleTypes
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import com.toolsandtaverns.paleolithicera.render.*
import com.toolsandtaverns.paleolithicera.screen.HideDryerScreen
import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
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
        BlockEntityRendererFactories.register(ModEntityType.CRUDE_CAMPFIRE, ::CrudeCampfireBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModEntityType.KNAPPING_STATION, ::KnappingStationBlockEntityRenderer)

        // Register the renderer for the wooden spear entity
        // This allows thrown spears to be properly displayed in the world
        EntityModelLayerRegistry.registerModelLayer(BoarModel.BOAR_MODEL_LAYER, BoarModel::texturedModelData)
        EntityModelLayerRegistry.registerModelLayer(IbexModel.IBEX_MODEL_LAYER, IbexModel::texturedModelData)

        EntityModelLayerRegistry.registerModelLayer(
            WoodenSpearProjectileModel.WOOD_SPEAR_MODEL_LAYER,
            WoodenSpearProjectileModel::texturedModelData
        )
        EntityModelLayerRegistry.registerModelLayer(
            BoneSpearProjectileModel.BONE_SPEAR_MODEL_LAYER,
            BoneSpearProjectileModel::texturedModelData
        )

        EntityRendererRegistry.register(ModEntityType.BOAR_ENTITY) { cxt -> BoarRenderer(cxt) }
        EntityRendererRegistry.register(ModEntityType.IBEX_ENTITY) { cxt -> IbexRenderer(cxt) }
        EntityRendererRegistry.register(ModEntityType.WOODEN_SPEAR_ENTITY) { cxt -> WoodenSpearRenderer(cxt) }
        EntityRendererRegistry.register(ModEntityType.BONE_SPEAR_ENTITY) { cxt -> BoneSpearRenderer(cxt) }
        
        // Register simple renderer for pebble entity using FlyingItemEntityRenderer
        EntityRendererRegistry.register(ModEntityType.PEBBLE_ENTITY) { ctx ->
            net.minecraft.client.render.entity.FlyingItemEntityRenderer(ctx)
        }
        
        // Register particle factories for custom particle effects
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.PEBBLE_IMPACT, PebbleImpactParticle::Factory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.STONE_DUST, StoneDustParticle::Factory)

        // Register the screen for the knapping station
        // This connects the container handler to its GUI implementation

        HandledScreens.register(ModScreenHandlers.KNAPPING, ::KnappingStationScreen)
        HandledScreens.register(ModScreenHandlers.HIDE_DRYER, ::HideDryerScreen)

        // Set the render layers for blocks with transparency
        // CUTOUT is used for blocks with binary transparency (fully transparent or fully opaque pixels)
        BlockRenderLayerMap.putBlock(ModBlocks.CRUDE_CAMPFIRE, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.ELDERBERRY_BUSH, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.CHAMOMILE_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.YARROW_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.WILD_GARLIC_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.EPHEDRA_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.SAGEBRUSH_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.WILD_MINT_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.WILD_GARLIC_PLANT, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.WILLOW_SAPLING, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.WILLOW_LEAF_VINES, BlockRenderLayer.CUTOUT)

        // Register client-side network handlers for the harpoon fishing system
        OpenHarpoonGuiClient.register()
        ToolTipEvents.register()
    }
}
