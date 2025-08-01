package com.toolsandtaverns.paleolithicera.client.renderer

import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemDisplayContext
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.RotationAxis

/**
 * Renders the input and output ItemStacks on top of the Knapping Station block.
 */
class KnappingStationBlockEntityRenderer(
    context: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<KnappingStationBlockEntity> {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        entity: KnappingStationBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d?
    ) {
        val input: ItemStack = entity.getInventory().getStack(0)
        val output: ItemStack = entity.getInventory().getStack(1)

        val lightLevel = WorldRenderer.getLightmapCoordinates(entity.world, entity.pos.up())

        if (!input.isEmpty) {
            matrices.push()
            matrices.translate(0.4, 1.01, 0.35)
            matrices.scale(0.4f, 0.4f, 0.4f)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
            itemRenderer.renderItem(
                input,
                ItemDisplayContext.FIXED,
                lightLevel,
                overlay,
                matrices,
                vertexConsumers,
                entity.world,
                0
            )
            matrices.pop()
        }

        if (!output.isEmpty) {
            matrices.push()
            matrices.translate(0.7, 1.01, 0.75)
            matrices.scale(0.4f, 0.4f, 0.4f)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
            itemRenderer.renderItem(
                output,
                ItemDisplayContext.FIXED,
                lightLevel,
                overlay,
                matrices,
                vertexConsumers,
                entity.world,
                0
            )
            matrices.pop()
        }
    }
}
