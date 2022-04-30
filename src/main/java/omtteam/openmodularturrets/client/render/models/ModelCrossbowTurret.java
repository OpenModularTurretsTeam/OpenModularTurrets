package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCrossbowTurret extends ModelAbstractTurret {
    private final ModelRenderer weapon; // the weapon itself

    public ModelCrossbowTurret() {
        super(0, 0, 0);

        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 24.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 0, -6.0F, -1.0F, -6.0F, 12, 1, 12, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 0, 23, -2.0F, -4.0F, -2.0F, 4, 3, 4, 0.0F, false));

        holder = new ModelRenderer(this);
        holder.setRotationPoint(0.0F, -4.0F, 0.0F);
        base.addChild(holder);
        holder.cubeList.add(new ModelBox(holder, 0, 30, -4.0F, -1.0F, -4.0F, 8, 1, 8, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 0, 39, 4.0F, -8.0F, -4.0F, 1, 8, 8, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 24, 31, -5.0F, -8.0F, -4.0F, 1, 8, 8, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 34, 30, 1.5F, -6.0F, 1.0F, 3, 1, 1, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 40, 30, -4.5F, -6.0F, 1.0F, 3, 1, 1, 0.0F, false));

        weapon = new ModelRenderer(this);
        weapon.setRotationPoint(0.0F, -5.5F, 1.5F);
        holder.addChild(weapon);
        weapon.cubeList.add(new ModelBox(weapon, 20, 13, -1.5F, -0.5F, -11.5F, 3, 1, 14, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 12, 23, -3.5F, -0.5F, -13.5F, 7, 1, 2, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 5, 1, 3.5F, -0.5F, -12.5F, 2, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 0, 2, 4.5F, -0.5F, -11.5F, 2, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 0, 4, -5.5F, -0.5F, -12.5F, 2, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 0, 0, -6.5F, -0.5F, -11.5F, 2, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 0, 13, -6.0F, 0.0F, -10.5F, 12, 0, 10, 0.0F, false));
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setRotationForTarget(float y, float z) {
        weapon.rotateAngleX = y;
        holder.rotateAngleY = z;
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
    public boolean hasPole() {
        return false;
    }

    @Override
    public boolean hasHolder() {
        return true;
    }
}
