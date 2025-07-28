package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.client.renderer.WoodenSpearRenderer
import com.toolsandtaverns.paleolithicera.entity.projectile.WoodenSpearEntity
import com.toolsandtaverns.paleolithicera.render.CrudeCampfireBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.registry.ModEntities
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.BlockRenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.entity.EntityRendererFactory

object PaleolithicEraClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererFactories.register(
            ModEntities.CRUDE_CAMPFIRE,
            ::CrudeCampfireBlockEntityRenderer
        )

        EntityRendererRegistry.register(ModEntities.SPEAR_ENTITY) { context ->
            WoodenSpearRenderer(context)
        }

        HandledScreens.register(ModScreenHandlers.KNAPPING, ::KnappingStationScreen)
        BlockRenderLayerMap.putBlock(ModBlocks.CRUDE_CAMPFIRE, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.ELDERBERRY_BUSH, BlockRenderLayer.CUTOUT)

    }
}
