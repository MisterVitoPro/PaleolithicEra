package com.toolsandtaverns.paleolithicera.model

import com.toolsandtaverns.paleolithicera.render.BoarRenderState
import com.toolsandtaverns.paleolithicera.util.id
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.ModelTransformer

// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
class BoarModel(root: ModelPart) : EntityModel<BoarRenderState>(root) {
    private val body: ModelPart = root.getChild("body")
    private val tail: ModelPart = body.getChild("tail")
    private val head: ModelPart = root.getChild("head")
    private val mouth: ModelPart = head.getChild("mouth")
    private val tusk: ModelPart = head.getChild("tusk")
    private val font_leg_l: ModelPart = root.getChild("font_leg_l")
    private val front_leg_r: ModelPart = root.getChild("front_leg_r")
    private val rear_leg_l: ModelPart = root.getChild("rear_leg_l")
    private val rear_leg_r: ModelPart = root.getChild("rear_leg_r")

    val BABY_TRANSFORMER: ModelTransformer = ModelTransformer.scaling(0.45f)

    private val walkingAnimation = BoarAnimations.walk.createAnimation(root)
    private val idlingAnimation = BoarAnimations.idle.createAnimation(root)

    override fun setAngles(state: BoarRenderState) {
        super.setAngles(state)
        this.head.pitch = state.pitch * (Math.PI.toFloat() / 180f)
        this.head.yaw = state.relativeHeadYaw * (Math.PI.toFloat() / 180f)

        this.walkingAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2f, 2.5f)
        this.idlingAnimation.apply(state.idleAnimationState, state.age, 1f)
    }

    companion object {
        val BOAR_MODEL_LAYER: EntityModelLayer = EntityModelLayer(id("boar"), "main")

        val texturedModelData: TexturedModelData
            get() {
                val modelData = ModelData()
                val modelPartData = modelData.root
                val body = modelPartData.addChild(
                    "body",
                    ModelPartBuilder.create().uv(28, 35).cuboid(-3.0f, -4.0f, 7.0f, 7.0f, 8.0f, 3.0f, Dilation(0.0f))
                        .uv(0, 0).cuboid(-4.0f, -6.0f, -6.0f, 9.0f, 10.0f, 13.0f, Dilation(0.0f))
                        .uv(0, 23).cuboid(-3.0f, -7.0f, -5.0f, 7.0f, 1.0f, 10.0f, Dilation(0.0f))
                        .uv(0, 42).cuboid(-2.0f, -8.0f, -4.0f, 5.0f, 1.0f, 6.0f, Dilation(0.0f))
                        .uv(0, 34).cuboid(-3.0f, 3.85f, -5.0f, 7.0f, 1.0f, 7.0f, Dilation(0.0f)),
                    ModelTransform.origin(0.0f, 15.0f, 0.0f)
                )

                val tail = body.addChild("tail", ModelPartBuilder.create(), ModelTransform.origin(0.5f, -3.0f, 9.5f))

                val tail_r1 = tail.addChild(
                    "tail_r1",
                    ModelPartBuilder.create().uv(30, 46).cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, 1.0f, 1.0f, 1.0036f, 0.0f, 0.0f)
                )

                val head = modelPartData.addChild(
                    "head",
                    ModelPartBuilder.create().uv(34, 23).cuboid(-3.5f, -4.0f, -3.0f, 7.0f, 8.0f, 4.0f, Dilation(0.0f))
                        .uv(44, 0).cuboid(-2.0f, 0.0f, -6.0f, 4.0f, 3.0f, 3.0f, Dilation(0.0f)),
                    ModelTransform.origin(0.5f, 14.0f, -7.0f)
                )

                val mouth =
                    head.addChild("mouth", ModelPartBuilder.create(), ModelTransform.origin(-0.25f, 3.0f, -3.75f))

                val mouth_r1 = mouth.addChild(
                    "mouth_r1",
                    ModelPartBuilder.create().uv(44, 12)
                        .cuboid(-3.15f, -0.9838f, 0.4451f, 2.0f, 1.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(2.15f, 1.0f, -2.15f, 0.3927f, 0.0f, 0.0f)
                )

                val tusk = head.addChild(
                    "tusk",
                    ModelPartBuilder.create().uv(34, 46).cuboid(3.9f, -0.9f, -0.4f, 1.0f, 2.0f, 1.0f, Dilation(0.0f))
                        .uv(38, 46).cuboid(-0.1f, -0.9f, -0.4f, 1.0f, 2.0f, 1.0f, Dilation(0.0f)),
                    ModelTransform.origin(-2.4f, 2.0f, -3.6f)
                )

                val cube_r1 = tusk.addChild(
                    "cube_r1",
                    ModelPartBuilder.create().uv(44, 15).cuboid(0.0f, -1.0f, -1.0f, 1.0f, 1.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(4.9f, -1.0f, -1.0f, -0.9424f, -0.3272f, 0.4164f)
                )

                val cube_r2 = tusk.addChild(
                    "cube_r2",
                    ModelPartBuilder.create().uv(44, 18).cuboid(-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -1.0f, -1.0f, -0.9424f, 0.3272f, -0.4164f)
                )

                val font_leg_l = modelPartData.addChild(
                    "font_leg_l",
                    ModelPartBuilder.create().uv(9, 50).cuboid(0.25f, 0.0f, -1.25f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(2.5f, 19.0f, -4.5f)
                )

                val front_leg_r = modelPartData.addChild(
                    "front_leg_r",
                    ModelPartBuilder.create().uv(9, 50).cuboid(-1.25f, 0.0f, -1.5f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(-2.5f, 19.0f, -4.25f)
                )

                val rear_leg_l = modelPartData.addChild(
                    "rear_leg_l",
                    ModelPartBuilder.create().uv(43, 6).cuboid(-2.0f, -1.0f, -1.0f, 3.0f, 3.0f, 3.0f, Dilation(0.0f))
                        .uv(0, 50).cuboid(-1.5f, 1.0f, -0.5f, 2.0f, 4.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(3.0f, 19.0f, 5.5f)
                )

                val rear_leg_r = modelPartData.addChild(
                    "rear_leg_r",
                    ModelPartBuilder.create().uv(50, 12).cuboid(-1.0f, -1.0f, -1.0f, 3.0f, 3.0f, 3.0f, Dilation(0.0f))
                        .uv(0, 50).cuboid(-0.5f, 1.0f, -0.5f, 2.0f, 4.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.origin(-2.0f, 19.0f, 5.5f)
                )
                return TexturedModelData.of(modelData, 64, 64)
            }
    }
}