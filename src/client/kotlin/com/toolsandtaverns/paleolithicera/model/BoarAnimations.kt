package com.toolsandtaverns.paleolithicera.model

import net.minecraft.client.render.entity.animation.AnimationDefinition
import net.minecraft.client.render.entity.animation.AnimationHelper
import net.minecraft.client.render.entity.animation.Keyframe
import net.minecraft.client.render.entity.animation.Transformation

/**
 * Made with Blockbench 4.12.5
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author MisterVitoPro
 */
object BoarAnimations {

    val idle: AnimationDefinition = AnimationDefinition.Builder.create(3.0f).looping()
        .addBoneAnimation(
            "head", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    0.5f,
                    AnimationHelper.createRotationalVector(15.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    1.0f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    1.6667f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    2.0417f,
                    AnimationHelper.createRotationalVector(-12.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    2.4167f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                )
            )
        )
        .addBoneAnimation(
            "tail", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    1.4167f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    2.0f,
                    AnimationHelper.createRotationalVector(22.5f, -22.5f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    2.375f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    2.5f,
                    AnimationHelper.createRotationalVector(22.5f, -22.5f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    2.7917f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    3.0f,
                    AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                )
            )
        )
        .build()

    val walk: AnimationDefinition = AnimationDefinition.Builder.create(1.5f).looping()
        .addBoneAnimation(
            "font_leg_l", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    0.75f,
                    AnimationHelper.createRotationalVector(25.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    1.5f,
                    AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                )
            )
        )
        .addBoneAnimation(
            "front_leg_r", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    0.75f,
                    AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    1.5f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                )
            )
        )
        .addBoneAnimation(
            "rear_leg_l", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    0.75f,
                    AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    1.5f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                )
            )
        )
        .addBoneAnimation(
            "rear_leg_r", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    0.75f,
                    AnimationHelper.createRotationalVector(30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                ),
                Keyframe(
                    1.5f,
                    AnimationHelper.createRotationalVector(-30.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.LINEAR
                )
            )
        )
        .build()
}