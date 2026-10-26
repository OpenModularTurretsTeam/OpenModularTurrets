package omtteam.openmodularturrets.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;

/**
 * The melee turret: a static base plate and shaft with a top piece and four blades that spin while the turret is
 * active. The blades are flat; the active texture draws them extended.
 */
public class MeleeTurretModel extends TurretModel {
    private static final ResourceLocation TEXTURE = OMT.id("textures/block/turret/melee_turret.png");
    private static final ResourceLocation TEXTURE_ACTIVE = OMT.id("textures/block/turret/melee_turret_extended.png");

    private final ModelPart top;
    private boolean active;

    public MeleeTurretModel(ModelPart root) {
        super(root);
        this.top = root.getChild("base").getChild("top");
    }

    @Override
    public ResourceLocation getTexture() {
        return active ? TEXTURE_ACTIVE : TEXTURE;
    }

    /** No culling, so the flat blades are visible from both sides. */
    @Override
    protected boolean cull() {
        return false;
    }

    /** The melee turret does not aim. */
    @Override
    public void setAim(float yawDegrees, float pitchDegrees) {
    }

    @Override
    public void setActivity(boolean active, float spinDegrees) {
        this.active = active;
        top.yRot = (float) Math.toRadians(spinDegrees);
    }
}
