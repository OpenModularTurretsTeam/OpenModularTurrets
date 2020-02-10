package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

@SideOnly(Side.CLIENT)
public class ModelRelativisticTurret extends ModelAbstractTurret {
    // fields
    public final ModelRenderer Spike1;
    public final ModelRenderer Spike2;
    public final ModelRenderer Spike3;
    public final ModelRenderer Spike4;
    public final ModelRenderer Base2;
    public final ModelRenderer Crystal;

    public ModelRelativisticTurret() {
        super(37, 28, 15);

        Base.addBox(-6F, 7F, -6F, 12, 1, 12);
        Base.setRotationPoint(0F, 16F, 0F);
        Base.setTextureSize(64, 64);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        Spike1 = new ModelRenderer(this, 24, 0);
        Spike1.addBox(-6F, 0F, -6F, 1, 8, 1);
        Spike1.setRotationPoint(0F, 15F, 0F);
        Spike1.setTextureSize(64, 64);
        Spike1.mirror = true;
        setRotation(Spike1, 0F, 0F, 0F);
        Spike2 = new ModelRenderer(this, 24, 0);
        Spike2.addBox(-6F, 0F, 5F, 1, 8, 1);
        Spike2.setRotationPoint(0F, 15F, 0F);
        Spike2.setTextureSize(64, 64);
        Spike2.mirror = true;
        setRotation(Spike2, 0F, 0F, 0F);
        Spike3 = new ModelRenderer(this, 24, 0);
        Spike3.addBox(5F, 0F, -6F, 1, 8, 1);
        Spike3.setRotationPoint(0F, 15F, 0F);
        Spike3.setTextureSize(64, 64);
        Spike3.mirror = true;
        setRotation(Spike3, 0F, 0F, 0F);
        Spike4 = new ModelRenderer(this, 24, 0);
        Spike4.addBox(5F, 0F, 5F, 1, 8, 1);
        Spike4.setRotationPoint(0F, 15F, 0F);
        Spike4.setTextureSize(64, 64);
        Spike4.mirror = true;
        setRotation(Spike4, 0F, 0F, 0F);
        Base2 = new ModelRenderer(this, 0, 0);
        Base2.addBox(-2F, 6F, -2F, 4, 2, 4);
        Base2.setRotationPoint(0F, 15F, 0F);
        Base2.setTextureSize(64, 64);
        Base2.mirror = true;
        setRotation(Base2, 0F, 0F, 0F);
        Crystal = new ModelRenderer(this, 0, 25);
        Crystal.addBox(-2F, -2F, -2F, 4, 4, 4);
        Crystal.setRotationPoint(0F, 15F, 0F);
        Crystal.setTextureSize(64, 64);
        Crystal.mirror = true;
        setRotation(Crystal, 0.7853982F, 0.7853982F, 0.7853982F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Base.render(f5);
        Spike1.render(f5);
        Spike2.render(f5);
        Spike3.render(f5);
        Spike4.render(f5);
        Base2.render(f5);
        Crystal.render(f5);
    }

    @Override
    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationForTarget(float y, float z) {

    }

    @Override
    public void setBaseRotation(TurretHead turretHead) {
        super.setBaseRotation(turretHead);
        Spike1.rotateAngleX = turretHead.baseFitRotationX;
        Spike1.rotateAngleY = turretHead.baseFitRotationZ;
        Spike2.rotateAngleX = turretHead.baseFitRotationX;
        Spike2.rotateAngleY = turretHead.baseFitRotationZ;
        Spike3.rotateAngleX = turretHead.baseFitRotationX;
        Spike3.rotateAngleY = turretHead.baseFitRotationZ;
        Spike4.rotateAngleX = turretHead.baseFitRotationX;
        Spike4.rotateAngleY = turretHead.baseFitRotationZ;
        Base2.rotateAngleX = turretHead.baseFitRotationX;
        Base2.rotateAngleY = turretHead.baseFitRotationZ;
        Crystal.rotateAngleX = turretHead.baseFitRotationX;
        Crystal.rotateAngleY = turretHead.baseFitRotationZ;
        Crystal.rotateAngleX = turretHead.rotationAnimation;
        Crystal.rotateAngleY = turretHead.rotationAnimation;
        Crystal.rotateAngleZ = turretHead.rotationAnimation;
    }

    public void renderAll() {
        Base.render(0.0625F);
        Spike1.render(0.0625F);
        Spike2.render(0.0625F);
        Spike3.render(0.0625F);
        Spike4.render(0.0625F);
        Base2.render(0.0625F);
        Crystal.render(0.0625F);
    }

    @Override
    public boolean hasPole() {
        return false;
    }

    @Override
    public boolean hasBoxUnder() {
        return false;
    }
}
