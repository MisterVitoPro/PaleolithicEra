package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.CrudeCampfireBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemDisplayContext
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

/**
 * Custom renderer for the Crude Campfire. Displays cooking items above the fire.
 */
class CrudeCampfireBlockEntityRenderer(
    context: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<CrudeCampfireBlockEntity> {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    /**
     * Renders the items cooking on the crude campfire.
     * 
     * This method positions and renders each item being cooked in the campfire,
     * properly oriented based on the campfire's facing direction.
     * 
     * @param entity The crude campfire block entity
     * @param tickDelta Partial tick time for smooth animations
     * @param matrices Transformation matrix stack
     * @param vertexConsumers Provider for vertex consumers
     * @param light The light level for rendering
     * @param overlay The overlay texture coordinates
     * @param cameraPos The position of the camera
     */
    override fun render(
        entity: CrudeCampfireBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d?
    ) {
        // Safety check for null values
        if (entity.world == null || matrices == null || vertexConsumers == null) return
        val state: BlockState = entity.cachedState

        // Only render items if the campfire is lit
        if (!state.get(CampfireBlock.LIT)) return

        // Get the campfire's facing direction to properly position items
        val facing: Direction = state.get(CampfireBlock.FACING)
        val rotationBase: Int = facing.horizontalQuarterTurns

        // Render each cooking item
        for ((index, stack) in entity.itemsBeingCooked.withIndex()) {
            // Skip empty slots
            if (stack == null || stack.isEmpty) continue

            matrices.push() // Save the current transformation state

            // Calculate the rotation angle based on item position and campfire facing
            val angle = Math.floorMod(index + rotationBase, 4)
            val direction = Direction.fromHorizontalQuarterTurns(angle)

            // Calculate offset to position items in a circle around the campfire
            // Uses a combination of the main direction and perpendicular direction
            // to position items in a square pattern around the center
            val offsetX = -direction.offsetX * 0.3f + direction.rotateYClockwise().offsetX * 0.3f
            val offsetZ = -direction.offsetZ * 0.3f + direction.rotateYClockwise().offsetZ * 0.3f

            // Position the item above the campfire, slightly offset from center
            matrices.translate(0.5 + offsetX, 0.44921875, 0.5 + offsetZ)
            // Rotate the item based on its position around the campfire
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle * 90f))
            // Scale the item down to look appropriate on the campfire
            matrices.scale(0.375f, 0.375f, 0.375f)
            // Lay the item flat as if it's cooking on the surface
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))

            // Render the actual item
            itemRenderer.renderItem(
                stack,                  // The item to render
                ItemDisplayContext.FIXED, // Display as a fixed item in world
                light,                  // Light level from the parameter
                overlay,                // Overlay texture coordinates
                matrices,               // Transformation matrix
                vertexConsumers,       // Vertex buffer provider
                entity.world,          // The world for context
                0                      // Seed for random variations
            )

            matrices.pop() // Restore the previous transformation state
        }
    }
}
