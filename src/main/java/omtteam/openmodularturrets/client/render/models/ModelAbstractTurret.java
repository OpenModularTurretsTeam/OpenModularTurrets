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
    public final ModelRenderer Base;
    public final ModelRenderer Pole;
    public final ModelRenderer BoxUnder;

    public ModelAbstractTurret(int texOffYBase, int texOffYPole, int texOffYBoxUnder) {
        textureWidth = 64;
        textureHeight = 64;
        this.Base = new ModelRenderer(this, 0, texOffYBase);
        this.Pole = new ModelRenderer(this, 0, texOffYPole);
        this.BoxUnder = new ModelRenderer(this, 0, texOffYBoxUnder);
    }

    public abstract void setRotation(ModelRenderer model, float x, float y, float z);

    public void setBaseRotation(TurretHead turretHead) {
        Base.rotateAngleX = turretHead.baseFitRotationX;
        Base.rotateAngleY = turretHead.baseFitRotationZ;
        if (hasPole()) {
            Pole.rotateAngleX = turretHead.baseFitRotationX;
            Pole.rotateAngleY = turretHead.baseFitRotationZ;
        }
        if (hasBoxUnder()) {
            BoxUnder.rotateAngleX = turretHead.baseFitRotationX;
        }
    }

    @SuppressWarnings("unused")
    public abstract void renderAll();

    @SuppressWarnings("unused")
    public abstract void setRotationForTarget(float y, float z);

    public abstract boolean hasPole();

    public abstract boolean hasBoxUnder();
}
