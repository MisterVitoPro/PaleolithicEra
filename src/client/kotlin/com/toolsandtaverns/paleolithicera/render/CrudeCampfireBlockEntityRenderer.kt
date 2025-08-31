package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.CrudeCampfireBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

/**
 * Custom renderer for the Crude Campfire. Displays cooking items above the fire.
 */
class CrudeCampfireBlockEntityRenderer(
    context: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<CrudeCampfireBlockEntity> {
    private val itemRenderer = context.itemRenderer

    private fun tryRenderFixed(stack: net.minecraft.item.ItemStack, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int, world: net.minecraft.world.World) {
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
            // ignore
        }
    }

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
        overlay: Int
    ) {
        val world = entity.world ?: return
        if (matrices == null || vertexConsumers == null) return
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
            tryRenderFixed(stack, matrices, vertexConsumers, light, overlay, world)
            matrices.pop()
        }
    }
}
