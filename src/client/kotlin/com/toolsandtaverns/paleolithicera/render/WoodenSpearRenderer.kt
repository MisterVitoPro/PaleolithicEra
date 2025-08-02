package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.WoodenSpearEntity
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemDisplayContext
import net.minecraft.util.math.RotationAxis

/**
 * Renderer for Wooden Spear entity using item model.
 */
class WoodenSpearRenderer(
    context: EntityRendererFactory.Context
) : net.minecraft.client.render.entity.EntityRenderer<WoodenSpearEntity, SpearRenderState>(context) {

    // Use the Minecraft item renderer to render the spear item model
    private val itemRenderer = MinecraftClient.getInstance().itemRenderer

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

        // Render the spear using the item model
        itemRenderer.renderItem(
            ModItems.WOODEN_SPEAR.defaultStack, // The item to render
            ItemDisplayContext.GROUND,          // Display context (ground item)
            light,                              // Light level
            OverlayTexture.DEFAULT_UV,          // Standard overlay texture (damage, etc.)
            matrixStack,                        // Transformation matrix
            vertexConsumerProvider,             // Vertex buffer provider
            null,                               // No specific world (null is okay)
            0                                   // Seed for random variations
        )

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
class SpearRenderState : net.minecraft.client.render.entity.state.EntityRenderState() {
    /** Horizontal rotation (yaw) of the spear in degrees */
    var yaw: Float = 0f
    /** Vertical rotation (pitch) of the spear in degrees */
    var pitch: Float = 0f
}


