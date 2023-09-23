package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMachineGun extends ModelAbstractTurret {
    // fields
    private final ModelRenderer weapon;

    public ModelMachineGun() {
        super(37, 28, 15);
        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 16.0F, 0.0F);
        base.cubeList.add(new ModelBox(base, 0, 37, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, true));
        base.cubeList.add(new ModelBox(base, 0, 28, -2.0F, 4.0F, -2.0F, 4, 4, 4, 0.0F, true));

        holder = new ModelRenderer(this);
        holder.setRotationPoint(0.0F, 0.0F, 0.0F);
        base.addChild(holder);
        holder.cubeList.add(new ModelBox(holder, 0, 0, -4.0F, -2.0F, 0.0F, 8, 1, 1, 0.0F, true));
        holder.cubeList.add(new ModelBox(holder, 0, 15, -4.0F, 3.0F, -4.0F, 8, 1, 8, 0.0F, true));

        ModelRenderer boxRight_r1 = new ModelRenderer(this);
        boxRight_r1.setRotationPoint(0.0F, 8.0F, 0.0F);
        holder.addChild(boxRight_r1);
        setRotation(boxRight_r1, 0.0F, 0.0F, 1.5708F);
        boxRight_r1.cubeList.add(new ModelBox(boxRight_r1, 0, 15, -12.0F, 4.0F, -4.0F, 8, 1, 8, 0.0F, true));
        boxRight_r1.cubeList.add(new ModelBox(boxRight_r1, 0, 15, -12.0F, -5.0F, -4.0F, 8, 1, 8, 0.0F, true));

        weapon = new ModelRenderer(this);
        weapon.setRotationPoint(0.0F, -1.0F, 0.0F);
        holder.addChild(weapon);
        weapon.cubeList.add(new ModelBox(weapon, 32, 21, -1.0F, 0.0F, -15.0F, 2, 2, 14, 0.0F, true));
        weapon.cubeList.add(new ModelBox(weapon, 36, 0, -2.0F, -5.0F, -2.0F, 4, 8, 8, 0.0F, true));
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
