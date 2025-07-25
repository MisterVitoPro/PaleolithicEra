package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.render.CrudeCampfireBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.BlockRenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object PaleolithicEraClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererFactories.register(
            ModBlockEntities.CRUDE_CAMPFIRE,
            ::CrudeCampfireBlockEntityRenderer
        )

        HandledScreens.register(ModScreenHandlers.KNAPPING, ::KnappingStationScreen)
        BlockRenderLayerMap.putBlock(ModBlocks.CRUDE_CAMPFIRE, BlockRenderLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.ELDERBERRY_BUSH, BlockRenderLayer.CUTOUT)

    }
}
