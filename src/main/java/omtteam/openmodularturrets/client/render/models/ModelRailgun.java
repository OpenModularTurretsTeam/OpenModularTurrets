package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRailgun extends ModelAbstractTurret {
    // fields
    private final ModelRenderer BarrelTop;
    private final ModelRenderer BarrelBot;
    private final ModelRenderer BarrelRight;
    private final ModelRenderer BarrelLeft;
    private final ModelRenderer BodyBot;
    private final ModelRenderer BodyTop;
    private final ModelRenderer Binder;
    private final ModelRenderer RightGuard;
    private final ModelRenderer LeftGuard;
    private final ModelRenderer GuardBinder;

    public ModelRailgun() {
        super(0, 0, 0);
        Base.addBox(-6F, 7F, -6F, 12, 1, 12);
        Base.setRotationPoint(0F, 16F, 0F);
        Base.setTextureSize(64, 64);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        BarrelTop = new ModelRenderer(this, 25, 27);
        BarrelTop.addBox(-1F, 2F, -16F, 2, 1, 17);
        BarrelTop.setRotationPoint(0F, 15F, 0F);
        BarrelTop.setTextureSize(64, 64);
        BarrelTop.mirror = true;
        setRotation(BarrelTop, 0F, 0F, 0F);
        BarrelBot = new ModelRenderer(this, 25, 27);
        BarrelBot.addBox(-1F, -1F, -16F, 2, 1, 17);
        BarrelBot.setRotationPoint(0F, 15F, 0F);
        BarrelBot.setTextureSize(64, 64);
        BarrelBot.mirror = true;
        setRotation(BarrelBot, 0F, 0F, 0F);
        BarrelRight = new ModelRenderer(this, 25, 45);
        BarrelRight.addBox(-2F, -1F, -16F, 1, 2, 17);
        BarrelRight.setRotationPoint(0F, 15F, 0F);
        BarrelRight.setTextureSize(64, 64);
        BarrelRight.mirror = true;
        setRotation(BarrelRight, 0F, 0F, 0F);
        BarrelLeft = new ModelRenderer(this, 25, 45);
        BarrelLeft.addBox(1F, -1F, -16F, 1, 2, 17);
        BarrelLeft.setRotationPoint(0F, 15F, 0F);
        BarrelLeft.setTextureSize(64, 64);
        BarrelLeft.mirror = true;
        setRotation(BarrelLeft, 0F, 0F, 0F);
        BodyBot = new ModelRenderer(this, 0, 29);
        BodyBot.addBox(-3F, 2F, 0F, 6, 2, 6);
        BodyBot.setRotationPoint(0F, 15F, 0F);
        BodyBot.setTextureSize(64, 64);
        BodyBot.mirror = true;
        setRotation(BodyBot, 0F, 0F, 0F);
        BodyTop = new ModelRenderer(this, 0, 37);
        BodyTop.addBox(-3F, -3F, 1F, 6, 4, 6);
        BodyTop.setRotationPoint(0F, 15F, 0F);
        BodyTop.setTextureSize(64, 64);
        BodyTop.mirror = true;
        setRotation(BodyTop, 0F, 0F, 0F);
        Binder = new ModelRenderer(this, 0, 21);
        Binder.addBox(-1F, 1F, 3F, 2, 1, 1);
        Binder.setRotationPoint(0F, 15F, 0F);
        Binder.setTextureSize(64, 64);
        Binder.mirror = true;
        setRotation(Binder, 0F, 0F, 0F);
        RightGuard = new ModelRenderer(this, 0, 47);
        RightGuard.addBox(-6.1F, -5F, -3F, 1, 8, 8);
        RightGuard.setRotationPoint(0F, 15F, 0F);
        RightGuard.setTextureSize(64, 64);
        RightGuard.mirror = true;
        setRotation(RightGuard, 0F, 0F, 0F);
        LeftGuard = new ModelRenderer(this, 0, 47);
        LeftGuard.addBox(5.1F, -5F, -3F, 1, 8, 8);
        LeftGuard.setRotationPoint(0F, 15F, 0F);
        LeftGuard.setTextureSize(64, 64);
        LeftGuard.mirror = true;
        setRotation(LeftGuard, 0F, 0F, 0F);
        GuardBinder = new ModelRenderer(this, 0, 25);
        GuardBinder.addBox(-6F, -0.9F, 0F, 12, 1, 1);
        GuardBinder.setRotationPoint(0F, 15F, 0F);
        GuardBinder.setTextureSize(64, 64);
        GuardBinder.mirror = true;
        setRotation(GuardBinder, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        Base.render(f5);
        BarrelTop.render(f5);
        BarrelBot.render(f5);
        BarrelRight.render(f5);
        BarrelLeft.render(f5);
        BodyBot.render(f5);
        BodyTop.render(f5);
        Binder.render(f5);
        RightGuard.render(f5);
        LeftGuard.render(f5);
        GuardBinder.render(f5);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setRotationForTarget(float y, float z) {
        BarrelTop.rotateAngleY = z;
        BarrelTop.rotateAngleX = y;
        BarrelBot.rotateAngleY = z;
        BarrelBot.rotateAngleX = y;
        BarrelRight.rotateAngleY = z;
        BarrelRight.rotateAngleX = y;
        BarrelLeft.rotateAngleY = z;
        BarrelLeft.rotateAngleX = y;
        BodyBot.rotateAngleY = z;
        BodyBot.rotateAngleX = y;
        BodyTop.rotateAngleY = z;
        BodyTop.rotateAngleX = y;
        GuardBinder.rotateAngleY = z;
        GuardBinder.rotateAngleX = y;
        RightGuard.rotateAngleY = z;
        RightGuard.rotateAngleX = y;
        LeftGuard.rotateAngleY = z;
        LeftGuard.rotateAngleX = y;
    }

    public void renderAll() {
        Base.render(0.0625F);
        BarrelTop.render(0.0625F);
        BarrelBot.render(0.0625F);
        BarrelRight.render(0.0625F);
        BarrelLeft.render(0.0625F);
        BodyBot.render(0.0625F);
        BodyTop.render(0.0625F);
        Binder.render(0.0625F);
        RightGuard.render(0.0625F);
        LeftGuard.render(0.0625F);
        GuardBinder.render(0.0625F);
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
