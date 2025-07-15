package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.block.entity.CrudeCampfireBlockEntity
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
class CrudeCampfireBlockEntityRenderer(context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<CrudeCampfireBlockEntity> {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        entity: CrudeCampfireBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d?
    ) {
        if (entity.world == null || matrices == null || vertexConsumers == null) return
        val state: BlockState = entity.cachedState

        if (!state.get(CampfireBlock.LIT)) return

        val facing: Direction = state.get(CampfireBlock.FACING)
        val rotationBase: Int = facing.horizontalQuarterTurns

        for ((index, stack) in entity.itemsBeingCooked.withIndex()) {
            if (stack == null || stack.isEmpty) continue

            matrices.push()

            val angle = Math.floorMod(index + rotationBase, 4)
            val direction = Direction.fromHorizontalQuarterTurns(angle)

            val offsetX = -direction.offsetX * 0.3f + direction.rotateYClockwise().offsetX * 0.3f
            val offsetZ = -direction.offsetZ * 0.3f + direction.rotateYClockwise().offsetZ * 0.3f

            matrices.translate(0.5 + offsetX, 0.44921875, 0.5 + offsetZ)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle * 90f))
            matrices.scale(0.375f, 0.375f, 0.375f)

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))

            itemRenderer.renderItem(
                stack,
                ItemDisplayContext.FIXED,
                light,
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
