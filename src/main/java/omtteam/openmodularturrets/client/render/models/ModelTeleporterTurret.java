package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTeleporterTurret extends ModelAbstractTurret {

    public ModelTeleporterTurret() {
        super(37, 28, 15);
        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 0, -2.0F, -3.0F, -2.0F, 4, 10, 4, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 14, -5.0F, -2.0F, -2.0F, 1, 8, 4, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 26, -2.0F, -2.0F, 4.0F, 4, 8, 1, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 26, -2.0F, -2.0F, -5.0F, 4, 8, 1, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 14, 4.0F, -2.0F, -2.0F, 1, 8, 4, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 51, -6.0F, -4.0F, -6.0F, 12, 1, 12, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 37, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, true));
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

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
