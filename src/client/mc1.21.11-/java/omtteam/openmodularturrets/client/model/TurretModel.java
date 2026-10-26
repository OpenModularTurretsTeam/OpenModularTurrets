package omtteam.openmodularturrets.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.ResourceLocation;

/**
 * A turret model (1.12.2 {@code ModelAbstractTurret}). Models are built like the 1.12.2 Java models (Y down, the base
 * plate at the bottom); {@link omtteam.openmodularturrets.client.render.blockentity.TurretRenderer} orients them on
 * the mounting face.
 * <p>
 * The variant for 1.21.11: drawing is deferred, so one model shared by all turrets of a type must not be posed when a
 * turret is submitted. The pose travels in a {@link Pose}, which the renderer applies in {@link #setupAnim} right
 * before it draws.
 */
public abstract class TurretModel extends Model<TurretModel.Pose> {
    /**
     * What a model shows: the aim (degrees, see {@link omtteam.openmodularturrets.core.turret.TurretMath}), whether the
     * turret is active (its base is active and it has a target) and the current spin angle in degrees.
     */
    public record Pose(float yawDegrees, float pitchDegrees, boolean active, float spinDegrees) {
        /** A turret as an item: not aiming, not active. */
        public static final Pose ITEM = new Pose(0, 0, false, 0);
    }

    protected TurretModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
    }

    public abstract ResourceLocation getTexture();

    /**
     * Points the model: yaw turns around the mounting face normal, pitch raises the weapon (degrees, see
     * {@link omtteam.openmodularturrets.core.turret.TurretMath}).
     */
    public abstract void setAim(float yawDegrees, float pitchDegrees);

    /** Sets whether the turret is active and the current spin angle in degrees. Only the melee turret uses this. */
    public void setActivity(boolean active, float spinDegrees) {
    }

    /** Whether back faces are culled; the flat parts of some models must be seen from both sides. */
    protected boolean cull() {
        return true;
    }

    public RenderType getRenderType() {
        return cull() ? RenderTypes.entityCutout(getTexture()) : RenderTypes.entityCutoutNoCull(getTexture());
    }

    /**
     * The render type for a pose: the texture of a model can depend on it (the melee turret's blades). Called when the
     * frame is extracted, the model is posed again before it is drawn.
     */
    public RenderType renderTypeFor(Pose pose) {
        setActivity(pose.active(), pose.spinDegrees());
        return getRenderType();
    }

    @Override
    public void setupAnim(Pose pose) {
        setAim(pose.yawDegrees(), pose.pitchDegrees());
        setActivity(pose.active(), pose.spinDegrees());
    }
}
