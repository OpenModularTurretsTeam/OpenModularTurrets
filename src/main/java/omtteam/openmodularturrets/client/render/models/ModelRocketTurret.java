package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRocketTurret extends ModelAbstractTurret {
    // fields
    private final ModelRenderer BoxLeft;
    private final ModelRenderer BoxRight;
    private final ModelRenderer CrossBar;
    private final ModelRenderer Launcher;
    private final ModelRenderer Missile1;
    private final ModelRenderer Missile2;

    public ModelRocketTurret() {
        super(37, 28, 15);

        base.addBox(-6F, 7F, -6F, 12, 1, 12);
        base.setRotationPoint(0F, 16F, 0F);
        base.setTextureSize(64, 32);
        base.mirror = true;
        setRotation(base, 0F, 0F, 0F);
        pole.addBox(-2F, 4F, -2F, 4, 4, 4);
        pole.setRotationPoint(0F, 16F, 0F);
        pole.setTextureSize(64, 32);
        pole.mirror = true;
        setRotation(pole, 0F, 0F, 0F);
        holder.addBox(-4F, 3F, -4F, 8, 1, 8);
        holder.setRotationPoint(0F, 16F, 0F);
        holder.setTextureSize(64, 32);
        holder.mirror = true;
        setRotation(holder, 0F, 0F, 0F);
        BoxLeft = new ModelRenderer(this, 0, 15);
        BoxLeft.addBox(-4F, 4F, -4F, 8, 1, 8);
        BoxLeft.setRotationPoint(0F, 16F, 0F);
        BoxLeft.setTextureSize(64, 64);
        BoxLeft.mirror = true;
        setRotation(BoxLeft, 0F, 0F, 1.570796F);
        BoxRight = new ModelRenderer(this, 0, 15);
        BoxRight.addBox(-4F, -5F, -4F, 8, 1, 8);
        BoxRight.setRotationPoint(0F, 16F, 0F);
        BoxRight.setTextureSize(64, 64);
        BoxRight.mirror = true;
        setRotation(BoxRight, 0F, 0F, 1.570796F);
        CrossBar = new ModelRenderer(this, 0, 0);
        CrossBar.addBox(-4F, -2F, 0F, 8, 1, 1);
        CrossBar.setRotationPoint(0F, 16F, 0F);
        CrossBar.setTextureSize(64, 64);
        CrossBar.mirror = true;
        setRotation(CrossBar, 0F, 0F, 0F);
        Launcher = new ModelRenderer(this, 36, 0);
        Launcher.addBox(-2F, -5F, -2F, 4, 8, 8);
        Launcher.setRotationPoint(0F, 15F, 0F);
        Launcher.setTextureSize(64, 64);
        Launcher.mirror = true;
        setRotation(Launcher, 0F, 0F, 0F);
        Missile1 = new ModelRenderer(this, 0, 6);
        Missile1.addBox(-1F, -5F, -4F, 2, 2, 2);
        Missile1.setRotationPoint(0F, 16F, 0F);
        Missile1.setTextureSize(64, 64);
        Missile1.mirror = true;
        setRotation(Missile1, 0F, 0F, 0F);
        Missile2 = new ModelRenderer(this, 0, 6);
        Missile2.addBox(-1F, -1F, -4F, 2, 2, 2);
        Missile2.setRotationPoint(0F, 16F, 0F);
        Missile2.setTextureSize(64, 64);
        Missile2.mirror = true;
        setRotation(Missile2, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        base.render(f5);
        pole.render(f5);
        holder.render(f5);
        BoxLeft.render(f5);
        BoxRight.render(f5);
        CrossBar.render(f5);
        Launcher.render(f5);
        Missile1.render(f5);
        Missile2.render(f5);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderAll() {
        base.render(0.0625F);
        pole.render(0.0625F);
        holder.render(0.0625F);
        BoxLeft.render(0.0625F);
        BoxRight.render(0.0625F);
        CrossBar.render(0.0625F);
        Launcher.render(0.0625F);
        Missile1.render(0.0625F);
        Missile2.render(0.0625F);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setRotationForTarget(float y, float z) {
        holder.rotateAngleY = z;
        BoxLeft.rotateAngleX = z;
        BoxRight.rotateAngleX = z;
        CrossBar.rotateAngleX = y;
        CrossBar.rotateAngleY = z;
        Launcher.rotateAngleX = y;
        Launcher.rotateAngleY = z;
        Missile1.rotateAngleX = y;
        Missile1.rotateAngleY = z;
        Missile2.rotateAngleX = y;
        Missile2.rotateAngleY = z;
    }

    @Override
    public boolean hasPole() {
        return true;
    }

    @Override
    public boolean hasHolder() {
        return true;
    }
}
