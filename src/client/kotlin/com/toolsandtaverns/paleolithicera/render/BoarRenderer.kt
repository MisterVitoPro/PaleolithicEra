package com.toolsandtaverns.paleolithicera.render

import com.toolsandtaverns.paleolithicera.entity.BoarEntity
import com.toolsandtaverns.paleolithicera.model.BoarModel
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class BoarRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<BoarEntity, BoarRenderState, BoarModel>(context, BoarModel(context.getPart(BoarModel.BOAR_MODEL_LAYER)), 0.25f) {

    override fun getTexture(state: BoarRenderState): Identifier {
        return id("textures/entity/boar.png")
    }

    override fun createRenderState(): BoarRenderState {
        return BoarRenderState()
    }

    override fun render(
        state: BoarRenderState, matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider?, i: Int
    ) {
        if (state.baby) {
            matrixStack.scale(0.45f, 0.45f, 0.45f)
        } else {
            matrixStack.scale(1f, 1f, 1f)
        }

        super.render(state, matrixStack, vertexConsumerProvider, i)
    }

    override fun updateRenderState(boarEntity: BoarEntity, livingEntityRenderState: BoarRenderState, f: Float) {
        super.updateRenderState(boarEntity, livingEntityRenderState, f)
        livingEntityRenderState.idleAnimationState.copyFrom(boarEntity.idleAnimationState)
    }
}