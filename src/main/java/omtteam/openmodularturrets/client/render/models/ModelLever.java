package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLever extends ModelBase {
    public final ModelRenderer Shape1;
    public final ModelRenderer Shape2;

    public ModelLever() {
        textureWidth = 32;
        textureHeight = 32;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(-1F, -1.0F, -9F, 2, 2, 8);
        Shape1.setRotationPoint(0F, 16F, 0F);
        Shape1.setTextureSize(32, 32);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);

        Shape2 = new ModelRenderer(this, 22, 0);
        Shape2.addBox(-1F, 0.0F, -3F, 2, 6, 2);
        Shape2.setRotationPoint(0F, 16F, 0F);
        Shape2.setTextureSize(32, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Shape1.render(f5);
        Shape2.render(f5);
    }

    @SuppressWarnings("SameParameterValue")
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderAll() {
        Shape1.render(0.0625F);
        Shape2.render(0.0625F);
    }
}
