package omtteam.openmodularturrets.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * A turret model (1.12.2 {@code ModelAbstractTurret}). Models are built like the 1.12.2 Java models (Y down, the base
 * plate at the bottom); {@link omtteam.openmodularturrets.client.render.blockentity.TurretRenderer} orients them on
 * the mounting face.
 */
public abstract class TurretModel {
    protected final ModelPart root;

    protected TurretModel(ModelPart root) {
        this.root = root;
    }

    public abstract ResourceLocation getTexture();

    /**
     * Points the model: yaw turns around the mounting face normal, pitch raises the weapon (degrees, see
     * {@link omtteam.openmodularturrets.core.turret.TurretMath}).
     */
    public abstract void setAim(float yawDegrees, float pitchDegrees);

    /**
     * Sets whether the turret is active (its base is active and it has a target) and the current spin angle in
     * degrees. Only the melee turret uses this.
     */
    public void setActivity(boolean active, float spinDegrees) {
    }

    /** Whether back faces are culled; the flat parts of some models must be seen from both sides. */
    protected boolean cull() {
        return true;
    }

    public RenderType getRenderType() {
        return cull() ? RenderType.entityCutout(getTexture()) : RenderType.entityCutoutNoCull(getTexture());
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
        root.render(poseStack, consumer, packedLight, packedOverlay);
    }
}
