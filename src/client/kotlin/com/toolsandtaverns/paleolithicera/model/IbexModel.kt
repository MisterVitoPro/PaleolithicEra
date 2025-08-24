package com.toolsandtaverns.paleolithicera.model

import com.toolsandtaverns.paleolithicera.entity.IbexEntity
import com.toolsandtaverns.paleolithicera.render.BoarRenderState
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.state.LivingEntityRenderState

// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
class IbexModel(root: ModelPart) : EntityModel<LivingEntityRenderState>(root) {

//    private val body: ModelPart = root.getChild("body")
//    private val front_leg_L: ModelPart = root.getChild("front_leg_L")
//    private val front_leg_R: ModelPart = root.getChild("front_leg_R")
//    private val back_leg_R: ModelPart = root.getChild("back_leg_R")
//    private val back_leg_L: ModelPart = root.getChild("back_leg_L")
    private val head: ModelPart = root.getChild("head")
//    private val bone: ModelPart = this.head.getChild("bone")
//    private val bone2: ModelPart = this.head.getChild("bone2")

    private val walkingAnimation = IbexAnimations.walk.createAnimation(root)

    override fun setAngles(state: LivingEntityRenderState) {
        super.setAngles(state)
        this.head.pitch = state.pitch * (Math.PI.toFloat() / 180f)
        this.head.yaw = state.relativeHeadYaw * (Math.PI.toFloat() / 180f)

        this.walkingAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2f, 2.5f)
    }

    companion object {
        val IBEX_MODEL_LAYER: EntityModelLayer = EntityModelLayer(id("ibex"), "main")

        val texturedModelData: TexturedModelData
            get() {
                val modelData = ModelData()
                val modelPartData = modelData.root
                val body = modelPartData.addChild(
                    "body",
                    ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -12.0f, -1.0f, 10.0f, 9.0f, 19.0f, Dilation(0.0f))
                        .uv(0, 28).cuboid(-2.0f, -13.0f, -3.0f, 4.0f, 11.0f, 9.0f, Dilation(0.0f)),
                    ModelTransform.origin(0.0f, 22.0f, 0.0f)
                )

                body.addChild(
                    "cube_r1",
                    ModelPartBuilder.create().uv(50, 36).cuboid(-1.5f, -1.0f, -1.0f, 1.0f, 1.0f, 3.0f, Dilation(0.0f)),
                    ModelTransform.of(1.0f, -10.0f, 18.0f, -1.0908f, 0.0f, 0.0f)
                )

                modelPartData.addChild(
                    "front_leg_L",
                    ModelPartBuilder.create().uv(0, 48).cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(3.0f, 19.0f, 1.0f)
                )

                modelPartData.addChild(
                    "front_leg_R",
                    ModelPartBuilder.create().uv(8, 48).cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(-3.0f, 19.0f, 1.0f)
                )

                modelPartData.addChild(
                    "back_leg_R",
                    ModelPartBuilder.create().uv(16, 48).cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(-3.0f, 19.0f, 16.0f)
                )

                modelPartData.addChild(
                    "back_leg_L",
                    ModelPartBuilder.create().uv(50, 28).cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(3.0f, 19.0f, 16.0f)
                )

                val head = modelPartData.addChild(
                    "head",
                    ModelPartBuilder.create().uv(26, 28).cuboid(-3.0f, -6.0f, -5.0f, 6.0f, 8.0f, 6.0f, Dilation(0.0f))
                        .uv(26, 42).cuboid(-2.0f, -2.0f, -10.0f, 4.0f, 4.0f, 6.0f, Dilation(0.0f))
                        .uv(16, 56).cuboid(-1.0f, 2.0f, -9.75f, 2.0f, 1.0f, 2.0f, Dilation(0.0f))
                        .uv(50, 40).cuboid(0.0f, 3.0f, -9.75f, 1.0f, 1.0f, 1.0f, Dilation(0.0f)),
                    ModelTransform.origin(0.0f, 11.0f, -2.0f)
                )

                head.addChild(
                    "ear_r1",
                    ModelPartBuilder.create().uv(56, 56).cuboid(0.0f, -3.0f, 0.0f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                    ModelTransform.of(-3.25f, -5.5f, 0.0f, -0.2618f, 0.0f, -0.5236f)
                )

                head.addChild(
                    "ear_r2",
                    ModelPartBuilder.create().uv(56, 52).cuboid(-1.0f, -3.0f, 0.0f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                    ModelTransform.of(3.25f, -5.5f, 0.0f, -0.2618f, 0.0f, 0.5236f)
                )

                head.addChild(
                    "cube_r2",
                    ModelPartBuilder.create().uv(46, 42).cuboid(-2.0f, -2.0f, -3.0f, 4.0f, 4.0f, 6.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -2.0f, -6.0f, 0.6545f, 0.0f, 0.0f)
                )

                val bone = head.addChild(
                    "bone",
                    ModelPartBuilder.create(),
                    ModelTransform.of(0.5f, -6.0f, -1.0f, 0.0f, 0.1745f, 0.0f)
                )

                bone.addChild(
                    "antler_r1",
                    ModelPartBuilder.create().uv(40, 52).cuboid(0.0f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -3.0f, 8.0f, -2.5307f, 0.0f, 0.0f)
                )

                bone.addChild(
                    "antler_r2",
                    ModelPartBuilder.create().uv(32, 52).cuboid(0.0f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -4.0f, 3.0f, -1.5708f, 0.0f, 0.0f)
                )

                bone.addChild(
                    "antler_r3",
                    ModelPartBuilder.create().uv(24, 52).cuboid(0.0f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, 0.0f, 0.0f, -0.6109f, 0.0f, 0.0f)
                )

                val bone2 = head.addChild(
                    "bone2",
                    ModelPartBuilder.create(),
                    ModelTransform.of(-2.5f, -6.0f, -1.0f, 0.0f, -0.1745f, 0.0f)
                )

                bone2.addChild(
                    "antler_r4",
                    ModelPartBuilder.create().uv(8, 56).cuboid(0.0f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -3.0f, 8.0f, -2.5307f, 0.0f, 0.0f)
                )

                bone2.addChild(
                    "antler_r5",
                    ModelPartBuilder.create().uv(0, 56).cuboid(0.0f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -4.0f, 3.0f, -1.5708f, 0.0f, 0.0f)
                )

                bone2.addChild(
                    "antler_r6",
                    ModelPartBuilder.create().uv(48, 52).cuboid(0.0f, -5.0f, -1.0f, 2.0f, 6.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, 0.0f, 0.0f, -0.6109f, 0.0f, 0.0f)
                )
                return TexturedModelData.of(modelData, 128, 128)
            }
    }
}