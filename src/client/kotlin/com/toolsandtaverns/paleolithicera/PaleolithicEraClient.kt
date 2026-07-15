package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.client.render.IbexRenderer
import com.toolsandtaverns.paleolithicera.entity.IbexEntity
import com.toolsandtaverns.paleolithicera.events.ToolTipEvents
import com.toolsandtaverns.paleolithicera.model.BoarModel
import com.toolsandtaverns.paleolithicera.model.BoneSpearProjectileModel
import com.toolsandtaverns.paleolithicera.model.IbexModel
import com.toolsandtaverns.paleolithicera.model.WoodenSpearProjectileModel
import com.toolsandtaverns.paleolithicera.network.OpenHarpoonGuiClient
import com.toolsandtaverns.paleolithicera.particle.PebbleImpactParticle
import com.toolsandtaverns.paleolithicera.particle.StoneDustParticle
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import com.toolsandtaverns.paleolithicera.registry.ModParticleTypes
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import com.toolsandtaverns.paleolithicera.render.BoarRenderer
import com.toolsandtaverns.paleolithicera.render.BoneSpearRenderer
import com.toolsandtaverns.paleolithicera.render.CrudeCampfireBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.render.FoodDryerBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.render.KnappingStationBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.render.WoodenSpearRenderer
import com.toolsandtaverns.paleolithicera.screen.FoodDryerScreen
import com.toolsandtaverns.paleolithicera.screen.HideDryerScreen
import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import com.toolsandtaverns.paleolithicera.screen.GroundStorageScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
//? if >=1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
//?} else {
/*import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap*///?}
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.block.Block
import net.minecraft.client.gui.screen.ingame.HandledScreens
//? if >=1.21.6 {
import net.minecraft.client.render.BlockRenderLayer
//?} else {
/*import net.minecraft.client.render.RenderLayer*///?}
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

/**
 * Client-side initialization for the Paleolithic Era mod.
 *
 * This class handles all client-specific initialization such as registering
 * renderers, screens, and client-side network handlers.
 */
object PaleolithicEraClient : ClientModInitializer {
    private fun setCutout(block: Block) {
        //? if >=1.21.6 {
        BlockRenderLayerMap.putBlock(block, BlockRenderLayer.CUTOUT)
        //?} else {
        /*BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout())*///?}
    }

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
        // Register the custom renderer for the crude campfire block entity
        // This renderer displays cooking items above the campfire
        BlockEntityRendererFactories.register(ModEntityType.CRUDE_CAMPFIRE, ::CrudeCampfireBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModEntityType.KNAPPING_STATION, ::KnappingStationBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModEntityType.FOOD_DRYER_BLOCK_ENTITY, ::FoodDryerBlockEntityRenderer)

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
        HandledScreens.register(ModScreenHandlers.FOOD_DRYER, ::FoodDryerScreen)
        HandledScreens.register(ModScreenHandlers.GROUND_STORAGE, ::GroundStorageScreen)

        // Set the render layers for blocks with transparency
        // CUTOUT is used for blocks with binary transparency (fully transparent or fully opaque pixels)
        setCutout(ModBlocks.CRUDE_CAMPFIRE)
        setCutout(ModBlocks.ELDERBERRY_BUSH)
        setCutout(ModBlocks.CHAMOMILE_PLANT)
        setCutout(ModBlocks.YARROW_PLANT)
        setCutout(ModBlocks.WILD_GARLIC_PLANT)
        setCutout(ModBlocks.EPHEDRA_PLANT)
        setCutout(ModBlocks.SAGEBRUSH_PLANT)
        setCutout(ModBlocks.WILD_MINT_PLANT)
        setCutout(ModBlocks.WILD_GINGER_PLANT)
        setCutout(ModBlocks.WILLOW_SAPLING)
        setCutout(ModBlocks.WILLOW_LEAF_VINES)
        setCutout(ModBlocks.WILLOW_LEAVES)
        setCutout(ModBlocks.EFFIGY_OF_PROTECTION)

        // Register client-side network handlers for the harpoon fishing system
        OpenHarpoonGuiClient.register()
        
        
        ToolTipEvents.register()
    }
}
