package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
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
import net.minecraft.item.ItemStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

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
        //? if >1.21.4 {
        overlay: Int,
        cameraPos: Vec3d?
        //?} else {
        /*overlay: Int*///?}
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
        }

        if (!output.isEmpty) {
            matrices.push()
            matrices.translate(0.7, 1.01, 0.75)
            matrices.scale(0.4f, 0.4f, 0.4f)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
            itemRenderer.renderItem(
                output,
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
        }
    }
}
