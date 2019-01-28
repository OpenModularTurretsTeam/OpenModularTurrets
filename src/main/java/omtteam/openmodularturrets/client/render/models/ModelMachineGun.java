package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMachineGun extends ModelAbstractTurret {
    // fields
    private final ModelRenderer BoxLeft;
    private final ModelRenderer BoxRight;
    private final ModelRenderer CrossBar;
    private final ModelRenderer GunStock;
    private final ModelRenderer Shape1;

    public ModelMachineGun() {
        super(37, 28, 15);

        Base.addBox(-6F, 7F, -6F, 12, 1, 12);
        Base.setRotationPoint(0F, 16F, 0F);
        Base.setTextureSize(64, 32);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        Pole.addBox(-2F, 4F, -2F, 4, 4, 4);
        Pole.setRotationPoint(0F, 16F, 0F);
        Pole.setTextureSize(64, 32);
        Pole.mirror = true;
        setRotation(Pole, 0F, 0F, 0F);
        BoxUnder.addBox(-4F, 3F, -4F, 8, 1, 8);
        BoxUnder.setRotationPoint(0F, 16F, 0F);
        BoxUnder.setTextureSize(64, 32);
        BoxUnder.mirror = true;
        setRotation(BoxUnder, 0F, 0F, 0F);
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
        GunStock = new ModelRenderer(this, 36, 0);
        GunStock.addBox(-2F, -5F, -2F, 4, 8, 8);
        GunStock.setRotationPoint(0F, 15F, 0F);
        GunStock.setTextureSize(64, 64);
        GunStock.mirror = true;
        setRotation(GunStock, 0F, 0F, 0F);
        Shape1 = new ModelRenderer(this, 32, 21);
        Shape1.addBox(-1F, -2F, -15F, 2, 2, 14);
        Shape1.setRotationPoint(0F, 17F, 0F);
        Shape1.setTextureSize(64, 64);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Base.render(f5);
        Pole.render(f5);
        BoxUnder.render(f5);
        BoxLeft.render(f5);
        BoxRight.render(f5);
        CrossBar.render(f5);
        GunStock.render(f5);
        Shape1.render(f5);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setRotationForTarget(float y, float z) {
        BoxUnder.rotateAngleY = z;
        BoxLeft.rotateAngleX = z;
        BoxRight.rotateAngleX = z;
        CrossBar.rotateAngleX = y;
        CrossBar.rotateAngleY = z;
        GunStock.rotateAngleX = y;
        GunStock.rotateAngleY = z;
        Shape1.rotateAngleX = y;
        Shape1.rotateAngleY = z;
    }

    public void renderAll() {
        Base.render(0.0625F);
        Pole.render(0.0625F);
        BoxUnder.render(0.0625F);
        BoxLeft.render(0.0625F);
        BoxRight.render(0.0625F);
        CrossBar.render(0.0625F);
        GunStock.render(0.0625F);
        Shape1.render(0.0625F);
    }

    @Override
    public boolean hasPole() {
        return true;
    }

    @Override
    public boolean hasBoxUnder() {
        return true;
    }
}
