import net.minecraft.client.render.entity.animation.*

/**
 * Made with Blockbench 4.12.6
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Author
 */
object IbexAnimations {
    val walk: AnimationDefinition = AnimationDefinition.Builder.create(1.0f).looping()
        .addBoneAnimation(
            "front_leg_L", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(-37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    0.5f,
                    AnimationHelper.createRotationalVector(45.0f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    1.0f,
                    AnimationHelper.createRotationalVector(-37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                )
            )
        )
        .addBoneAnimation(
            "front_leg_R", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    0.5f,
                    AnimationHelper.createRotationalVector(-37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    1.0f,
                    AnimationHelper.createRotationalVector(37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                )
            )
        )
        .addBoneAnimation(
            "back_leg_R", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(-37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    0.5f,
                    AnimationHelper.createRotationalVector(37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    1.0f,
                    AnimationHelper.createRotationalVector(-37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                )
            )
        )
        .addBoneAnimation(
            "back_leg_L", Transformation(
                Transformation.Targets.ROTATE,
                Keyframe(
                    0.0f,
                    AnimationHelper.createRotationalVector(37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    0.5f,
                    AnimationHelper.createRotationalVector(-37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                ),
                Keyframe(
                    1.0f,
                    AnimationHelper.createRotationalVector(37.5f, 0.0f, 0.0f),
                    Transformation.Interpolations.CUBIC
                )
            )
        )
        .build()
}