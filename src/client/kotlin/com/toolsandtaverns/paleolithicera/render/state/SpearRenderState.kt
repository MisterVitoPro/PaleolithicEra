import net.minecraft.client.render.entity.state.EntityRenderState

/**
 * State class for wooden spear rendering.
 *
 * Stores rotation information for the spear entity that needs to be preserved
 * between update and render calls.
 */
class SpearRenderState : EntityRenderState() {
    /** Horizontal rotation (yaw) of the spear in degrees */
    var yaw: Float = 0f

    /** Vertical rotation (pitch) of the spear in degrees */
    var pitch: Float = 0f
}