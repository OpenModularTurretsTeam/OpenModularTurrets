package openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelLever;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.LeverTileEntity;

public class LeverRenderer extends TileEntitySpecialRenderer {

    private final ModelLever model;

    public LeverRenderer() {
        model = new ModelLever();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        LeverTileEntity lever = (LeverTileEntity) te;
        int rotation = 0;
        if (te.getWorldObj() != null) {
            rotation = te.getBlockMetadata();
        }

        ResourceLocation textures = (new ResourceLocation(ModInfo.ID + ":textures/blocks/leverBlock.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        GL11.glRotatef(rotation * 90, 0.0F, 1.0F, 0.0F);
        model.Shape1.rotateAngleZ = (lever.rotation / 55);
        model.Shape2.rotateAngleZ = (lever.rotation / 55);
        model.renderAll();
        GL11.glPopMatrix();
    }
}
