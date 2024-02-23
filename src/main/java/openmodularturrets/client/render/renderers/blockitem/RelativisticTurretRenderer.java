package openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelDamageAmp;
import openmodularturrets.client.render.models.ModelRedstoneReactor;
import openmodularturrets.client.render.models.ModelRelativisticTurret;
import openmodularturrets.client.render.models.ModelSolarPanelAddon;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.turrets.TurretHead;

public class RelativisticTurretRenderer extends TileEntitySpecialRenderer {

    private ModelSolarPanelAddon solar;
    private ModelDamageAmp amp;
    private ModelRedstoneReactor reac;
    private final ModelRelativisticTurret model;

    public RelativisticTurretRenderer() {
        model = new ModelRelativisticTurret();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        TurretHead turretHead = (TurretHead) te;
        int rotation = 0;
        if (te.getWorldObj() != null) {
            rotation = te.getBlockMetadata();
        }

        if (turretHead.shouldConceal) {
            return;
        }

        ResourceLocation textures = (new ResourceLocation(ModInfo.ID + ":textures/blocks/relativisticTurret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        GL11.glRotatef(rotation * 90, 0.0F, 1.0F, 0.0F);

        model.Base.rotateAngleX = turretHead.baseFitRotationX;
        model.Base.rotateAngleY = turretHead.baseFitRotationZ;
        model.Spike1.rotateAngleX = turretHead.baseFitRotationX;
        model.Spike1.rotateAngleY = turretHead.baseFitRotationZ;
        model.Spike2.rotateAngleX = turretHead.baseFitRotationX;
        model.Spike2.rotateAngleY = turretHead.baseFitRotationZ;
        model.Spike3.rotateAngleX = turretHead.baseFitRotationX;
        model.Spike3.rotateAngleY = turretHead.baseFitRotationZ;
        model.Spike4.rotateAngleX = turretHead.baseFitRotationX;
        model.Spike4.rotateAngleY = turretHead.baseFitRotationZ;
        model.Base2.rotateAngleX = turretHead.baseFitRotationX;
        model.Base2.rotateAngleY = turretHead.baseFitRotationZ;
        model.Crystal.rotateAngleX = turretHead.baseFitRotationX;
        model.Crystal.rotateAngleY = turretHead.baseFitRotationZ;
        model.Crystal.rotateAngleX = turretHead.rotationAnimation;
        model.Crystal.rotateAngleY = turretHead.rotationAnimation;
        model.Crystal.rotateAngleZ = turretHead.rotationAnimation;

        model.renderAll();
        GL11.glPopMatrix();
    }
}
