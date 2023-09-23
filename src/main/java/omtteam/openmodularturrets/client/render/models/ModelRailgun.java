package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRailgun extends ModelAbstractTurret {
    // fields
    private final ModelRenderer weapon;

    public ModelRailgun() {
        super(0, 0, 0);
        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 0, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, true));

        weapon = new ModelRenderer(this);
        weapon.setRotationPoint(0.0F, -1.0F, 0.0F);
        base.addChild(weapon);
        weapon.cubeList.add(new ModelBox(weapon, 25, 27, -1.0F, 2.0F, -16.0F, 2, 1, 17, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 25, 45, 1.0F, -1.0F, -16.0F, 1, 2, 17, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 25, 27, -1.0F, -1.0F, -16.0F, 2, 1, 17, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 25, 45, -2.0F, -1.0F, -16.0F, 1, 2, 17, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 0, 29, -3.0F, 2.0F, 0.0F, 6, 2, 6, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 0, 37, -3.0F, -3.0F, 1.0F, 6, 4, 6, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 0, 21, -1.0F, 1.0F, 3.0F, 2, 1, 1, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 0, 25, -6.0F, -0.9F, 0.0F, 12, 1, 1, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 0, 47, 5.1F, -5.0F, -3.0F, 1, 8, 8, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 0, 47, -6.1F, -5.0F, -3.0F, 1, 8, 8, 0.0F, true));
    }

    public void setRotationForTarget(float y, float z) {
        weapon.rotateAngleX = y;
        weapon.rotateAngleY = z;
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
    public boolean hasHolder() {
        return false;
    }
}

