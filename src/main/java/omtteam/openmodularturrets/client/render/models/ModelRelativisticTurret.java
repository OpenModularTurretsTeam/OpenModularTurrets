package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRelativisticTurret extends ModelAbstractTurret {
    // fields

    public ModelRelativisticTurret() {
        super(37, 28, 15);
        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 24, 0, -6.0F, -1.0F, -6.0F, 1, 8, 1, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 24, 0, -6.0F, -1.0F, 5.0F, 1, 8, 1, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 24, 0, 5.0F, -1.0F, -6.0F, 1, 8, 1, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 24, 0, 5.0F, -1.0F, 5.0F, 1, 8, 1, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 0, -2.0F, 5.0F, -2.0F, 4, 2, 4, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 37, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, true));

        ModelRenderer Crystal_r1;
        Crystal_r1 = new ModelRenderer(this);
        Crystal_r1.setRotationPoint(0.0F, -1.0F, 0.0F);
        base.addChild(Crystal_r1);
        setRotation(Crystal_r1, -0.7854F, 0.0F, 0.6109F);
        Crystal_r1.cubeList.add(new ModelBox(Crystal_r1, 0, 25, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, true));
    }

    @Override
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
