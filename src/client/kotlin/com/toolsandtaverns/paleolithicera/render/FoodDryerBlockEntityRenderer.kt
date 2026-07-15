package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.FoodDryerBlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
//? if >1.21.4 {
import net.minecraft.item.ItemDisplayContext
//?} else {
/*import net.minecraft.item.ModelTransformationMode*///?}
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import kotlin.math.sin

/**
 * Renders food items hanging on the Food Dryer structure.
 * 
 * This renderer displays items in 3D space hanging from the dryer framework,
 * simulating the traditional method of air-drying food. Each slot has a specific
 * position and items may gently sway to simulate hanging movement.
 */
class FoodDryerBlockEntityRenderer(
    context: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<FoodDryerBlockEntity> {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    companion object {
        // Positions for hanging items in each of the 4 slots
        // These positions correspond to hanging points on the dryer structure
        private val HANGING_POSITIONS = arrayOf(
            Vec3d(0.2, 0.65, 0.25),   // Slot 0: Front-left hanging from beam
            Vec3d(0.8, 0.65, 0.25),   // Slot 1: Front-right hanging from beam
            Vec3d(0.2, 0.65, 0.75),   // Slot 2: Back-left hanging from beam
            Vec3d(0.8, 0.65, 0.75)    // Slot 3: Back-right hanging from beam
        )
    }

    override fun render(
        entity: FoodDryerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        //? if >1.21.4 {
        overlay: Int,
        cameraPos: Vec3d?
        //?} else {
        /*overlay: Int*///?}
    ) {
        val renderData = entity.getRenderingData()
        if (renderData.isEmpty()) return

        val lightLevel = WorldRenderer.getLightmapCoordinates(entity.world, entity.pos.up())
        val time = entity.world?.time?.toFloat() ?: 0f

        for (data in renderData) {
            val position = HANGING_POSITIONS[data.slot]
            
            matrices.push()
            
            // Position the item at the hanging point
            matrices.translate(position.x, position.y, position.z)
            
            // Add gentle swaying motion based on progress and time (before scaling)
            val swayIntensity = 0.01f * (1f - data.progress) // Less sway as drying progresses
            val swayOffsetX = sin((time + data.slot * 30f) * 0.015f) * swayIntensity
            val swayOffsetZ = sin((time + data.slot * 40f) * 0.012f) * swayIntensity * 0.5f
            matrices.translate(swayOffsetX.toDouble(), 0.0, swayOffsetZ.toDouble())
            
            // Scale down the item to look more realistic hanging
            val scale = 0.4f
            matrices.scale(scale, scale, scale)
            
            // Rotate to hang vertically downward - items should point down
            // First rotate around X-axis to make items hang downward
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f))

            // Render the item
            itemRenderer.renderItem(
                data.stack,
                //? if >1.21.4 {
                ItemDisplayContext.FIXED,
                //?} else {
                /*ModelTransformationMode.FIXED,*///?}
                lightLevel,
                overlay,
                matrices,
                vertexConsumers,
                entity.world,
                0
            )
            
            matrices.pop()
            
            // Render hanging string/rope effect (optional visual enhancement)
            renderHangingString(matrices, vertexConsumers, position, light, overlay)
        }
    }

    /**
     * Renders a simple hanging string effect from the drying frame to the item.
     * This provides visual feedback that items are actually hanging.
     */
    private fun renderHangingString(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        itemPosition: Vec3d,
        light: Int,
        overlay: Int
    ) {
        // Simple implementation - could be enhanced with actual line rendering
        // For now, this is a placeholder that could render thin quads or lines
        // representing rope/string connecting the hanging frame to the items
        
        // This could be implemented using vertex buffers to draw thin lines
        // from the frame (y=0.95) down to the item position
        // For simplicity, we'll skip the actual line rendering in this implementation
    }

    override fun getRenderDistance(): Int {
        return 64 // Render distance in blocks
    }
}
