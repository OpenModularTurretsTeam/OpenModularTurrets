package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The melee turret (no model in 1.12.2; ported from 1.20.1+ {@code TurretModelDefinitions.meleeTurret}), laid out
 * after its 128×128 textures: base plate and shaft stay still, the {@code top} part (hub, cap and a flat 32×32 blade
 * cross through the cap's middle) spins while the turret is active.
 */
@SideOnly(Side.CLIENT)
public class ModelMeleeTurret extends ModelAbstractTurret {
    private final ModelRenderer top;

    public ModelMeleeTurret() {
        super(0, 0, 0);
        textureWidth = 128;
        textureHeight = 128;
        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 32, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 17, -2.0F, 4.0F, -2.0F, 4, 3, 4, 0.0F, false));

        top = new ModelRenderer(this);
        top.setRotationPoint(0.0F, 0.0F, 0.0F);
        base.addChild(top);
        top.cubeList.add(new ModelBox(top, 0, 10, -3.0F, 3.0F, -3.0F, 6, 1, 6, 0.0F, false));
        top.cubeList.add(new ModelBox(top, 0, 0, -4.0F, 1.0F, -4.0F, 8, 2, 8, 0.0F, false));
        top.cubeList.add(new ModelBox(top, 0, 0, -16.0F, 2.0F, -16.0F, 32, 0, 32, 0.0F, false));
    }

    @Override
    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    /** The melee turret does not aim. */
    @Override
    public void setRotationForTarget(float pitch, float yaw) {
    }

    @Override
    public void setActivity(boolean active, float spinDegrees) {
        top.rotateAngleY = (float) Math.toRadians(spinDegrees);
    }

    /** Without culling, so the flat blades are visible from both sides. */
    @Override
    public void renderAll() {
        GlStateManager.disableCull();
        base.render(0.0625F);
        GlStateManager.enableCull();
    }

    @Override
    public boolean hasHolder() {
        return false;
    }
}
