package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

@SideOnly(Side.CLIENT)
public class ModelTeleporterTurret extends ModelAbstractTurret {
    //fields
    public final ModelRenderer PillarLarge;
    public final ModelRenderer Spinner1;
    public final ModelRenderer Spinner2;
    public final ModelRenderer Spinner3;
    public final ModelRenderer Spinner4;
    private final ModelRenderer BaseStand;

    public ModelTeleporterTurret() {
        super(37, 28, 15);

        Base.addBox(-6F, 7F, -6F, 12, 1, 12);
        Base.setRotationPoint(0F, 16F, 0F);
        Base.setTextureSize(64, 64);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        BaseStand = new ModelRenderer(this, 0, 51);
        BaseStand.addBox(-6F, -1F, -6F, 12, 1, 12);
        BaseStand.setRotationPoint(0F, 13F, 0F);
        BaseStand.setTextureSize(64, 64);
        BaseStand.mirror = true;
        setRotation(BaseStand, 0F, 0F, 0F);
        PillarLarge = new ModelRenderer(this, 0, 0);
        PillarLarge.addBox(-2F, 0F, -2F, 4, 10, 4);
        PillarLarge.setRotationPoint(0F, 13F, 0F);
        PillarLarge.setTextureSize(64, 64);
        PillarLarge.mirror = true;
        setRotation(PillarLarge, 0F, 0F, 0F);
        Spinner1 = new ModelRenderer(this, 0, 14);
        Spinner1.addBox(-5F, 0F, -2F, 1, 8, 4);
        Spinner1.setRotationPoint(0F, 14F, 0F);
        Spinner1.setTextureSize(64, 64);
        Spinner1.mirror = true;
        setRotation(Spinner1, 0F, 0F, 0F);
        Spinner2 = new ModelRenderer(this, 0, 26);
        Spinner2.addBox(-2F, 0F, 4F, 4, 8, 1);
        Spinner2.setRotationPoint(0F, 14F, 0F);
        Spinner2.setTextureSize(64, 64);
        Spinner2.mirror = true;
        setRotation(Spinner2, 0F, 0F, 0F);
        Spinner3 = new ModelRenderer(this, 0, 26);
        Spinner3.addBox(-2F, 0F, -5F, 4, 8, 1);
        Spinner3.setRotationPoint(0F, 14F, 0F);
        Spinner3.setTextureSize(64, 64);
        Spinner3.mirror = true;
        setRotation(Spinner3, 0F, 0F, 0F);
        Spinner4 = new ModelRenderer(this, 0, 14);
        Spinner4.addBox(4F, 0F, -2F, 1, 8, 4);
        Spinner4.setRotationPoint(0F, 14F, 0F);
        Spinner4.setTextureSize(64, 64);
        Spinner4.mirror = true;
        setRotation(Spinner4, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Base.render(f5);
        BaseStand.render(f5);
        PillarLarge.render(f5);
        Spinner1.render(f5);
        Spinner2.render(f5);
        Spinner3.render(f5);
        Spinner4.render(f5);
    }

    @SuppressWarnings("SameParameterValue")
    @Override
    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    @Override
    public void setRotationForTarget(float y, float z) {

    }

    @Override
    public void setBaseRotation(TurretHead turretHead) {
        super.setBaseRotation(turretHead);
        Spinner1.rotateAngleY = turretHead.rotationAnimation;
        Spinner2.rotateAngleY = turretHead.rotationAnimation;
        Spinner3.rotateAngleY = turretHead.rotationAnimation;
        Spinner4.rotateAngleY = turretHead.rotationAnimation;
        PillarLarge.rotateAngleY = (turretHead.rotationAnimation) * -1;
    }

    public void renderAll() {
        Base.render(0.0625F);
        BaseStand.render(0.0625F);
        PillarLarge.render(0.0625F);
        Spinner1.render(0.0625F);
        Spinner2.render(0.0625F);
        Spinner3.render(0.0625F);
        Spinner4.render(0.0625F);
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
