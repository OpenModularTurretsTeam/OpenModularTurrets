package omtteam.openmodularturrets.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public abstract void renderAll();

    /**
     * Turns the weapon: {@code pitch} around the model's x axis (the weapon part), {@code yaw} around its y axis (the
     * holder), both in radians in model space.
     */
    public abstract void setRotationForTarget(float pitch, float yaw);

    /**
     * Whether the turret is active (base active and a target in reach) and its spin angle in degrees; only the melee
     * turret uses it.
     */
    public void setActivity(boolean active, float spinDegrees) {
    }

    //public void setRotationForTargetYawPitch(float yaw, float pitch);

    public abstract boolean hasHolder();
}
