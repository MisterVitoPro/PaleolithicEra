package com.toolsandtaverns.paleolithicera.model

import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.state.EntityRenderState

class WoodenSpearProjectileModel(root: ModelPart) : EntityModel<EntityRenderState>(root) {

    private val main: ModelPart = root.getChild("main")

    companion object {
        val WOOD_SPEAR_MODEL_LAYER: EntityModelLayer = EntityModelLayer(id("wooden_spear"), "main")
        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            modelData.root.addChild(
                "main",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -24.0F, 0.0F, 1.0F, 24.0F, 1.0F, Dilation(0.0f)),
                ModelTransform.origin(0.0f, 20f, 0.0f)
            )
            return TexturedModelData.of(modelData, 64, 64)
        }
    }

}