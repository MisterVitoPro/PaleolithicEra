package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.RotationAxis

/**
 * Renders the input and output ItemStacks on top of the Knapping Station block.
 */
class KnappingStationBlockEntityRenderer(
    context: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<KnappingStationBlockEntity> {

    private val itemRenderer = context.itemRenderer

    private fun tryRenderFixed(stack: ItemStack, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int, world: net.minecraft.world.World) {
        try {
            val clsNames = listOf(
                "net.minecraft.item.ItemDisplayContext",
                "net.minecraft.client.render.model.json.ModelTransformationMode",
                "net.minecraft.client.render.model.json.ModelTransformation\$Mode",
                "net.minecraft.client.render.item.ItemDisplayContext"
            )
            var enumCls: Class<*>? = null
            var fixedEnum: Any? = null
            for (n in clsNames) {
                try {
                    val c = Class.forName(n)
                    val f = c.getField("FIXED").get(null)
                    enumCls = c
                    fixedEnum = f
                    break
                } catch (_: Throwable) { }
            }
            if (enumCls == null || fixedEnum == null) return
            val m = itemRenderer.javaClass.methods.firstOrNull { m ->
                m.name == "renderItem" && m.parameterTypes.size == 8 &&
                    m.parameterTypes[0].name.endsWith("ItemStack") &&
                    m.parameterTypes[1] == enumCls &&
                    m.parameterTypes[2].name.endsWith("MatrixStack") &&
                    m.parameterTypes[3].name.contains("VertexConsumerProvider") &&
                    m.parameterTypes[4] == Int::class.javaPrimitiveType &&
                    m.parameterTypes[5] == Int::class.javaPrimitiveType &&
                    m.parameterTypes[6].name.endsWith("World") &&
                    m.parameterTypes[7] == Int::class.javaPrimitiveType
            } ?: return
            m.invoke(itemRenderer, stack, fixedEnum, matrices, vertexConsumers, light, overlay, world, 0)
        } catch (_: Throwable) {
            // Ignore rendering failure
        }
    }

    override fun render(
        entity: KnappingStationBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val input: ItemStack = entity.getInventory().getStack(0)
        val output: ItemStack = entity.getInventory().getStack(1)
        val world = entity.world ?: return

        val lightLevel = WorldRenderer.getLightmapCoordinates(entity.world, entity.pos.up())

        if (!input.isEmpty) {
            matrices.push()
            matrices.translate(0.4, 1.01, 0.35)
            matrices.scale(0.4f, 0.4f, 0.4f)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
            tryRenderFixed(input, matrices, vertexConsumers, lightLevel, overlay, world)
            matrices.pop()
        }

        if (!output.isEmpty) {
            matrices.push()
            matrices.translate(0.7, 1.01, 0.75)
            matrices.scale(0.4f, 0.4f, 0.4f)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
            tryRenderFixed(output, matrices, vertexConsumers, lightLevel, overlay, world)
            matrices.pop()
        }
    }
}
