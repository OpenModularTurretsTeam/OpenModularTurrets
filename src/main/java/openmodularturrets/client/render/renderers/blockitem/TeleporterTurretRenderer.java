package openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openmodularturrets.client.render.models.ModelDamageAmp;
import openmodularturrets.client.render.models.ModelIncendiaryTurret;
import openmodularturrets.client.render.models.ModelMachineGun;
import openmodularturrets.client.render.models.ModelRedstoneReactor;
import openmodularturrets.client.render.models.ModelSolarPanelAddon;
import openmodularturrets.client.render.models.ModelTeleporterTurret;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.turrets.TurretHead;
import openmodularturrets.util.TurretHeadUtils;

import org.lwjgl.opengl.GL11;

public class TeleporterTurretRenderer extends TileEntitySpecialRenderer {

    private ModelTeleporterTurret model;
    ModelSolarPanelAddon solar;
    ModelDamageAmp amp;
    ModelRedstoneReactor reac;

    public TeleporterTurretRenderer() {
        model = new ModelTeleporterTurret();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {

        TurretHead turretHead = (TurretHead) te;
        int rotation = 0;
        if (te.getWorldObj() != null) {
            rotation = te.getBlockMetadata();
        }

        this.model.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
        ResourceLocation textures = (new ResourceLocation(ModInfo.ID + ":textures/blocks/teleporterTurret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        GL11.glRotatef(rotation * 90, 0.0F, 1.0F, 0.0F);

        model.Base.rotateAngleX = turretHead.baseFitRotationX;
        model.Base.rotateAngleY = turretHead.baseFitRotationZ;
        
        model.Spinner1.rotateAngleY = turretHead.rotationAmimation;
        model.Spinner2.rotateAngleY = turretHead.rotationAmimation;
        model.Spinner3.rotateAngleY = turretHead.rotationAmimation;
        model.Spinner4.rotateAngleY = turretHead.rotationAmimation;

        model.renderAll();


        GL11.glPopMatrix();
    }
}
