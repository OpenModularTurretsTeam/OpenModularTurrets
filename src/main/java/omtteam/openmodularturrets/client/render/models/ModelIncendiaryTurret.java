package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelIncendiaryTurret extends ModelAbstractTurret {
    // fields
    private final ModelRenderer BoxLeft;
    private final ModelRenderer BoxRight;
    private final ModelRenderer CrossBar;
    private final ModelRenderer Barrel1;
    private final ModelRenderer Barrel2;
    private final ModelRenderer Tank;

    public ModelIncendiaryTurret() {
        super(37, 28, 15);

        Base.addBox(-6F, 7F, -6F, 12, 1, 12);
        Base.setRotationPoint(0F, 16F, 0F);
        Base.setTextureSize(64, 64);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        Pole.addBox(-2F, 0F, -2F, 4, 4, 4);
        Pole.setRotationPoint(0F, 19F, 0F);
        Pole.setTextureSize(64, 64);
        Pole.mirror = true;
        setRotation(Pole, 0F, 0F, 0F);
        BoxUnder.addBox(-4F, 3F, -4F, 8, 1, 8);
        BoxUnder.setRotationPoint(0F, 16F, 0F);
        BoxUnder.setTextureSize(64, 64);
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
        Barrel1 = new ModelRenderer(this, 0, 0);
        Barrel1.addBox(1F, -1F, -10F, 2, 2, 11);
        Barrel1.setRotationPoint(0F, 15F, 0F);
        Barrel1.setTextureSize(64, 64);
        Barrel1.mirror = true;
        setRotation(Barrel1, 0F, 0F, 0F);
        Barrel2 = new ModelRenderer(this, 0, 0);
        Barrel2.addBox(-3F, -1F, -10F, 2, 2, 11);
        Barrel2.setRotationPoint(0F, 15F, 0F);
        Barrel2.setTextureSize(64, 64);
        Barrel2.mirror = true;
        setRotation(Barrel2, 0F, 0F, 0F);
        Tank = new ModelRenderer(this, 29, 0);
        Tank.addBox(-3F, -3F, -6F, 6, 4, 10);
        Tank.setRotationPoint(0F, 16F, 0F);
        Tank.setTextureSize(64, 64);
        Tank.mirror = true;
        setRotation(Tank, 0F, 0F, 0F);
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
        Barrel1.render(f5);
        Barrel2.render(f5);
        Tank.render(f5);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderAll() {
        Base.render(0.0625F);
        Pole.render(0.0625F);
        BoxUnder.render(0.0625F);
        BoxLeft.render(0.0625F);
        BoxRight.render(0.0625F);
        CrossBar.render(0.0625F);
        Barrel1.render(0.0625F);
        Barrel2.render(0.0625F);
        Tank.render(0.0625F);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setRotationForTarget(float y, float z) {
        BoxUnder.rotateAngleY = z;
        BoxLeft.rotateAngleX = z;
        BoxRight.rotateAngleX = z;
        CrossBar.rotateAngleX = y;
        CrossBar.rotateAngleY = z;
        Barrel1.rotateAngleX = y;
        Barrel1.rotateAngleY = z;
        Barrel2.rotateAngleX = y;
        Barrel2.rotateAngleY = z;
        Tank.rotateAngleX = y;
        Tank.rotateAngleY = z;
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
