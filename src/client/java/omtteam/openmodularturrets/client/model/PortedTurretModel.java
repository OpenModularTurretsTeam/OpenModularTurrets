package omtteam.openmodularturrets.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * A turret model ported from 1.12.2. The old {@code setRotationForTarget} turned one part around Y (yaw) and one
 * around X (pitch); static turrets (arc, slowing, teleporter) have neither.
 */
public class PortedTurretModel extends TurretModel {
    private final ResourceLocation texture;
    @Nullable
    private final ModelPart yawPart;
    @Nullable
    private final ModelPart pitchPart;

    /**
     * @param yawPath   path of the part that turns, e.g. {@code base/holder}; {@code null} for static turrets
     * @param pitchPath path of the part that tilts, e.g. {@code base/holder/weapon}
     */
    public PortedTurretModel(ModelPart root, ResourceLocation texture, @Nullable String yawPath, @Nullable String pitchPath) {
        super(root);
        this.texture = texture;
        this.yawPart = find(root, yawPath);
        this.pitchPart = find(root, pitchPath);
    }

    @Nullable
    private static ModelPart find(ModelPart root, @Nullable String path) {
        if (path == null) return null;
        ModelPart part = root;
        for (String name : path.split("/")) {
            part = part.getChild(name);
        }
        return part;
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    /**
     * The model is flipped into the local frame (y and z negated), so the angles are negated. If yaw and pitch are on
     * the same part (railgun), {@code ModelPart} applies pitch first, then yaw, as needed.
     */
    @Override
    public void setAim(float yawDegrees, float pitchDegrees) {
        if (yawPart != null) yawPart.yRot = (float) Math.toRadians(-yawDegrees);
        if (pitchPart != null) pitchPart.xRot = (float) Math.toRadians(-pitchDegrees);
    }
}
