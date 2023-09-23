package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelArcTurret extends ModelAbstractTurret {

    public ModelArcTurret() {
        super(0, 0, 0);
        textureWidth = 64;
        textureHeight = 64;

        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 0, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 13, -3.0F, 3.0F, -3.0F, 6, 4, 6, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 23, -0.5F, -6.0F, -0.5F, 1, 9, 1, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 8, -1.0F, -8.0F, -1.0F, 2, 2, 2, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 18, 13, -2.0F, -1.25F, -2.0F, 4, 1, 4, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 4, -1.5F, -3.25F, -1.5F, 3, 1, 3, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 0, -1.5F, -5.0F, -1.5F, 3, 1, 3, -0.25F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        base.render(f5);
    }

    @Override
    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void renderAll() {
        base.render(0.0625F);
    }

    @Override
    public void setRotationForTarget(float y, float z) {

    }

    @Override
    public boolean hasHolder() {
        return false;
    }
}