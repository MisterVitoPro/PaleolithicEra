package com.toolsandtaverns.paleolithicera.client.renderer

import com.toolsandtaverns.paleolithicera.entity.projectile.WoodenSpearEntity
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.client.render.entity.state.TridentEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.projectile.TridentEntity
import net.minecraft.item.ItemDisplayContext
import net.minecraft.util.math.RotationAxis

/**
 * Renderer for Wooden Spear entity using item model.
 */
class WoodenSpearRenderer(
    context: EntityRendererFactory.Context
) : EntityRenderer<WoodenSpearEntity, SpearRenderState>(context) {

    private val itemRenderer = MinecraftClient.getInstance().itemRenderer

    override fun createRenderState(): SpearRenderState = SpearRenderState()

    override fun updateRenderState(entity: WoodenSpearEntity, state: SpearRenderState, tickDelta: Float) {
        super.updateRenderState(entity, state, tickDelta)
        state.yaw = entity.getLerpedYaw(tickDelta)
        state.pitch = entity.getLerpedPitch(tickDelta)
    }

    override fun render(
        state: SpearRenderState,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int
    ) {
        matrixStack.push()
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0f))
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch + 90.0f))
        itemRenderer.renderItem(
            ModItems.WOODEN_SPEAR.defaultStack,
            ItemDisplayContext.GROUND,
            light,
            OverlayTexture.DEFAULT_UV,
            matrixStack,
            vertexConsumerProvider,
            null,
            0
        )
        matrixStack.pop()
        super.render(state, matrixStack, vertexConsumerProvider, light)
    }
}

class SpearRenderState : EntityRenderState() {
    var yaw: Float = 0f
    var pitch: Float = 0f
}


