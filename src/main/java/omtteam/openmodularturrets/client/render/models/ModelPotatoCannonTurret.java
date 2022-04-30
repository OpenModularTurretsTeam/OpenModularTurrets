package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPotatoCannonTurret extends ModelAbstractTurret {
    // fields
    private final ModelRenderer BoxLeft;
    private final ModelRenderer BoxRight;
    private final ModelRenderer CrossBar;
    private final ModelRenderer Barrel;
    private final ModelRenderer Chamber;

    public ModelPotatoCannonTurret() {
        super(37, 28, 15);

        base.addBox(-6F, 7F, -6F, 12, 1, 12);
        base.setRotationPoint(0F, 16F, 0F);
        base.setTextureSize(64, 32);
        base.mirror = true;
        setRotation(base, 0F, 0F, 0F);
        pole.addBox(-2F, 4F, -2F, 4, 4, 4);
        pole.setRotationPoint(0F, 16F, 0F);
        pole.setTextureSize(64, 32);
        pole.mirror = true;
        setRotation(pole, 0F, 0F, 0F);
        holder.addBox(-4F, 3F, -4F, 8, 1, 8);
        holder.setRotationPoint(0F, 16F, 0F);
        holder.setTextureSize(64, 64);
        holder.mirror = true;
        setRotation(holder, 0F, 0F, 0F);
        BoxLeft = new ModelRenderer(this, 0, 15);
        BoxLeft.addBox(-4F, 4F, -4F, 8, 1, 8);
        BoxLeft.setRotationPoint(0F, 16F, 0F);
        BoxLeft.setTextureSize(64, 64);
        BoxLeft.mirror = true;
        setRotation(BoxLeft, 0F, 0F, 1.570796F);
        BoxRight = new ModelRenderer(this, 0, 15);
        BoxRight.addBox(-4F, -5F, -4F, 8, 1, 8);
        BoxRight.setRotationPoint(0F, 16F, 0F);
        BoxRight.setTextureSize(64, 64);
        BoxRight.mirror = true;
        setRotation(BoxRight, 0F, 0F, 1.570796F);
        CrossBar = new ModelRenderer(this, 0, 0);
        CrossBar.addBox(-4F, -2F, 0F, 8, 1, 1);
        CrossBar.setRotationPoint(0F, 16F, 0F);
        CrossBar.setTextureSize(64, 64);
        CrossBar.mirror = true;
        setRotation(CrossBar, 0F, 0F, 0F);
        Barrel = new ModelRenderer(this, 36, 0);
        Barrel.addBox(-1F, -2F, -11F, 2, 3, 12);
        Barrel.setRotationPoint(0F, 15F, 0F);
        Barrel.setTextureSize(64, 64);
        Barrel.mirror = true;
        setRotation(Barrel, 0F, 0F, 0F);
        Chamber = new ModelRenderer(this, 0, 4);
        Chamber.addBox(-2F, -3F, 1F, 4, 4, 4);
        Chamber.setRotationPoint(0F, 15F, 0F);
        Chamber.setTextureSize(64, 64);
        Chamber.mirror = true;
        setRotation(Chamber, 0F, 0F, 0F);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setRotationForTarget(float y, float z) {
        holder.rotateAngleY = z;
        BoxLeft.rotateAngleX = z;
        BoxRight.rotateAngleX = z;
        CrossBar.rotateAngleX = y;
        CrossBar.rotateAngleY = z;
        Barrel.rotateAngleX = y;
        Barrel.rotateAngleY = z;
        Chamber.rotateAngleX = y;
        Chamber.rotateAngleY = z;
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderAll() {
        base.render(0.0625F);
        pole.render(0.0625F);
        holder.render(0.0625F);
        BoxLeft.render(0.0625F);
        BoxRight.render(0.0625F);
        CrossBar.render(0.0625F);
        Barrel.render(0.0625F);
        Chamber.render(0.0625F);
    }

    @Override
    public boolean hasPole() {
        return true;
    }

    @Override
    public boolean hasHolder() {
        return true;
    }
}
