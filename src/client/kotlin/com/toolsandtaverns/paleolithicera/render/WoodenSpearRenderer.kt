package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.WoodenSpearEntity
import com.toolsandtaverns.paleolithicera.model.WoodenSpearProjectileModel
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

/**
 * Renderer for Wooden Spear entity using item model.
 */
class WoodenSpearRenderer(
    context: EntityRendererFactory.Context,
) : EntityRenderer<WoodenSpearEntity, SpearRenderState>(context) {

    val model: WoodenSpearProjectileModel = WoodenSpearProjectileModel(context.getPart(WoodenSpearProjectileModel.WOOD_SPEAR_MODEL_LAYER))

    /**
     * Creates a new render state object for this renderer.
     * 
     * @return A new SpearRenderState instance to store rendering state
     */
    override fun createRenderState(): SpearRenderState = SpearRenderState()

    /**
     * Updates the render state with entity positioning information.
     * 
     * This method interpolates between the entity's previous and current rotation values
     * to ensure smooth animations when rendering at different frame rates.
     * 
     * @param entity The wooden spear entity being rendered
     * @param state The render state to update
     * @param tickDelta Partial tick time for smooth animations
     */
    override fun updateRenderState(entity: WoodenSpearEntity, state: SpearRenderState, tickDelta: Float) {
        super.updateRenderState(entity, state, tickDelta)
        // Get interpolated yaw and pitch for smooth rotation
        state.yaw = entity.getLerpedYaw(tickDelta)
        state.pitch = entity.getLerpedPitch(tickDelta)
    }

    /**
     * Renders the wooden spear entity using the item model.
     * 
     * This method transforms the rendering matrix to position and orient the spear correctly,
     * then uses the item renderer to draw the actual spear model.
     * 
     * @param state The render state containing positioning data
     * @param matrixStack The transformation matrix stack
     * @param vertexConsumerProvider Provider for vertex consumers
     * @param light The light level for rendering
     */
    override fun render(
        state: SpearRenderState,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int
    ) {
        matrixStack.push() // Save the current transformation state

        // Apply rotations to match the entity's orientation in the world
        // Subtract 90 degrees from yaw to align with Minecraft's coordinate system
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0f))
        // Add 90 degrees to pitch to make the spear point forward correctly
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch + 90.0f))

        val vertexConsumer = ItemRenderer.getItemGlintConsumer(
            vertexConsumerProvider,
            this.model.getLayer(id("textures/entity/wooden_spear.png")),
            false,
            false
        )
        this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV)

        matrixStack.pop() // Restore the previous transformation state
        super.render(state, matrixStack, vertexConsumerProvider, light) // Call parent renderer
    }
}

/**
 * State class for wooden spear rendering.
 * 
 * Stores rotation information for the spear entity that needs to be preserved
 * between update and render calls.
 */
class SpearRenderState : EntityRenderState() {
    /** Horizontal rotation (yaw) of the spear in degrees */
    var yaw: Float = 0f
    /** Vertical rotation (pitch) of the spear in degrees */
    var pitch: Float = 0f
}


