package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

/**
 * Created by Keridos on 04/12/16.
 * This Class
 */

@SideOnly(Side.CLIENT)
public abstract class ModelAbstractTurret extends ModelBase {
    protected ModelRenderer base;
    protected ModelRenderer pole;
    protected ModelRenderer holder;

    public ModelAbstractTurret(int texOffYBase, int texOffYPole, int texOffHolder) {
        textureWidth = 64;
        textureHeight = 64;
        this.base = new ModelRenderer(this, 0, texOffYBase);
        this.pole = new ModelRenderer(this, 0, texOffYPole);
        this.holder = new ModelRenderer(this, 0, texOffHolder);
    }

    public abstract void setRotation(ModelRenderer model, float x, float y, float z);

    public void setBaseRotation(TurretHead turretHead) {
        base.rotateAngleX = turretHead.baseFitRotationX;
        base.rotateAngleY = turretHead.baseFitRotationZ;
    }

    public abstract void renderAll();

    public abstract void setRotationForTarget(float y, float z);

    //public void setRotationForTargetYawPitch(float yaw, float pitch);

    public abstract boolean hasPole();

    public abstract boolean hasHolder();
}
