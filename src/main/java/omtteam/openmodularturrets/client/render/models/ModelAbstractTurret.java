package omtteam.openmodularturrets.client.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Keridos on 04/12/16.
 * This Class
 */

@SideOnly(Side.CLIENT)
public abstract class ModelAbstractTurret extends ModelBase {
    @SuppressWarnings({"SameParameterValue", "unused"})
    protected abstract void setRotation(ModelRenderer model, float x, float y, float z);

    @SuppressWarnings("unused")
    public abstract void renderAll();

    @SuppressWarnings("unused")
    protected abstract void setRotationForTarget(float y, float z);
}
