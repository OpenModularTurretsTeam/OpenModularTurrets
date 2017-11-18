package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelLever;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import org.lwjgl.opengl.GL11;

class LeverRenderer extends TileEntitySpecialRenderer {
    private final ModelLever model;

    public LeverRenderer() {
        model = new ModelLever();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        LeverTileEntity lever = (LeverTileEntity) te;
        int rotation;
        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/lever_block.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        if (te == null) {
            GL11.glScalef(0.7F, -0.7F, -0.7F);
            GL11.glTranslatef((float) x + 0.0F, (float) y + 0.4F, (float) z + 0.5F);
            GL11.glRotatef(45.0F, 2.5F, -4.5F, -1.0F);
            model.renderAll();
            GL11.glPopMatrix();
            return;
        }

        if (te.getWorld() != null) {
            rotation = te.getBlockMetadata();
            GL11.glRotatef(rotation * -90F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, -1F, -1F);
            model.Shape1.rotateAngleZ = (lever.rotation / 55);
            model.Shape2.rotateAngleZ = (lever.rotation / 55);
            model.renderAll();
        }

        GL11.glPopMatrix();
    }
}
