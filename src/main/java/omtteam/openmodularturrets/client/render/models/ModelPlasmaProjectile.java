package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPlasmaProjectile extends ModelBase {
    private final ModelRenderer shot;

    public ModelPlasmaProjectile() {
        textureWidth = 16;
        textureHeight = 16;

        shot = new ModelRenderer(this);
        shot.setRotationPoint(0.0F, 22.0F, 0.0F);
        shot.cubeList.add(new ModelBox(shot, 0, 0, 0.0F, -2.0F, -2.0F, 0, 4, 4, 0.0F, false));
        shot.cubeList.add(new ModelBox(shot, 0, 0, -2.0F, -2.0F, 0.0F, 4, 4, 0, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        shot.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
