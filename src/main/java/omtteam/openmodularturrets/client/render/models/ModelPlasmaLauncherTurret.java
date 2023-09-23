package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPlasmaLauncherTurret extends ModelAbstractTurret {
    private final ModelRenderer weapon; // the weapon itself

    public ModelPlasmaLauncherTurret() {
        super(0, 0, 0);

        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 0, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, false));
        base.cubeList.add(new ModelBox(base, 38, 24, -2.0F, 4.0F, -2.0F, 4, 3, 4, 0.0F, false));

        holder = new ModelRenderer(this);
        holder.setRotationPoint(0.0F, 4.0F, 0.0F);
        base.addChild(holder);
        holder.cubeList.add(new ModelBox(holder, 14, 15, -4.0F, -1.0F, -4.0F, 8, 1, 8, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 0, 30, -5.0F, -8.0F, -4.0F, 1, 8, 8, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 28, 28, 4.0F, -8.0F, -4.0F, 1, 8, 8, 0.0F, false));
        holder.cubeList.add(new ModelBox(holder, 22, 13, -4.0F, -6.0F, 0.0F, 8, 1, 1, 0.0F, false));

        weapon = new ModelRenderer(this);
        weapon.setRotationPoint(0.0F, -5.0F, 0.0F);
        holder.addChild(weapon);
        weapon.cubeList.add(new ModelBox(weapon, 38, 13, -2.0F, -2.0F, -3.0F, 4, 4, 6, 0.5F, false));
        weapon.cubeList.add(new ModelBox(weapon, 36, 0, -1.0F, -1.0F, -12.0F, 2, 2, 9, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 12, 24, -2.0F, -2.0F, -11.0F, 4, 4, 8, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 0, 36, 0.0F, -2.5F, -12.0F, 0, 5, 10, 0.0F, false));
        weapon.cubeList.add(new ModelBox(weapon, 24, 44, -2.5F, 0.0F, -12.0F, 5, 0, 10, 0.0F, false));
    }

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
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f4, entity);
        base.render(f5);
    }

    @Override
    public boolean hasHolder() {
        return true;
    }
}
