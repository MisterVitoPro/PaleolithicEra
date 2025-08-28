package com.toolsandtaverns.paleolithicera.client.render

import com.toolsandtaverns.paleolithicera.entity.IbexEntity
import com.toolsandtaverns.paleolithicera.model.IbexModel
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.state.LivingEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class IbexRenderer(context: EntityRendererFactory.Context)
    : MobEntityRenderer<IbexEntity, LivingEntityRenderState, IbexModel>(
context,
    IbexModel(context.getPart(IbexModel.IBEX_MODEL_LAYER)),
0.5f
) {
    override fun getTexture(entity: LivingEntityRenderState): Identifier {
        return id("textures/entity/ibex.png")
    }

    override fun createRenderState(): LivingEntityRenderState {
        return LivingEntityRenderState()
    }

    override fun render(
        state: LivingEntityRenderState, matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider?, i: Int
    ) {
        if (state.baby) {
            matrixStack.scale(0.55f, 0.55f, 0.55f)
        } else {
            matrixStack.scale(1f, 1f, 1f)
        }

        super.render(state, matrixStack, vertexConsumerProvider, i)
    }

}
