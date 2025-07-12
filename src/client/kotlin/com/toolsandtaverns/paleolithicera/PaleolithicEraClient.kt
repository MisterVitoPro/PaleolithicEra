package com.toolsandtaverns.paleolithicera

import com.toolsandtaverns.paleolithicera.client.render.CrudeCampfireBlockEntityRenderer
import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.screen.KnappingStationScreen
import com.toolsandtaverns.paleolithicera.registry.ModScreenHandlers
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.BlockRenderLayer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.registry.BuiltinRegistries
import net.minecraft.registry.Registry

object PaleolithicEraClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererFactories.register(
            ModBlockEntities.CRUDE_CAMPFIRE,
            ::CrudeCampfireBlockEntityRenderer
        )

        HandledScreens.register(ModScreenHandlers.KNAPPING, ::KnappingStationScreen)
        BlockRenderLayerMap.putBlock(ModBlocks.CRUDE_CAMPFIRE, BlockRenderLayer.CUTOUT)

    }
}
