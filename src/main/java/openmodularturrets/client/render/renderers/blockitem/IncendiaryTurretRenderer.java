package openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelDamageAmp;
import openmodularturrets.client.render.models.ModelIncendiaryTurret;
import openmodularturrets.client.render.models.ModelRedstoneReactor;
import openmodularturrets.client.render.models.ModelSolarPanelAddon;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.turrets.TurretHead;
import openmodularturrets.util.TurretHeadUtil;

public class IncendiaryTurretRenderer extends TileEntitySpecialRenderer {

    private final ModelSolarPanelAddon solar;
    private final ModelDamageAmp amp;
    private final ModelRedstoneReactor reac;
    private final ModelIncendiaryTurret model;

    public IncendiaryTurretRenderer() {
        model = new ModelIncendiaryTurret();
        solar = new ModelSolarPanelAddon();
        amp = new ModelDamageAmp();
        reac = new ModelRedstoneReactor();
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

        this.model.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
        ResourceLocation textures = (new ResourceLocation(ModInfo.ID + ":textures/blocks/incendiaryTurret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        GL11.glRotatef(rotation * 90, 0.0F, 1.0F, 0.0F);

        model.Base.rotateAngleX = turretHead.baseFitRotationX;
        model.Base.rotateAngleY = turretHead.baseFitRotationZ;
        model.Pole.rotateAngleX = turretHead.baseFitRotationX;
        model.Pole.rotateAngleY = turretHead.baseFitRotationZ;
        model.BoxUnder.rotateAngleX = turretHead.baseFitRotationX;

        model.renderAll();

        if (turretHead.base != null) {
            if (TurretHeadUtil.hasSolarPanelAddon(turretHead.base)) {
                ResourceLocation texturesSolar = (new ResourceLocation(
                        ModInfo.ID + ":textures/blocks/solarPanelAddon" + ".png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesSolar);
                solar.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
                solar.renderAll();
            }

            if (TurretHeadUtil.hasDamageAmpAddon(turretHead.base)) {
                ResourceLocation texturesAmp = (new ResourceLocation(
                        ModInfo.ID + ":textures/blocks/damageAmpAddon" + ".png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesAmp);
                amp.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
                amp.renderAll();
            }

            if (TurretHeadUtil.hasRedstoneReactor(turretHead.base)) {
                ResourceLocation texturesReac = (new ResourceLocation(
                        ModInfo.ID + ":textures/blocks/redstoneReactor" + ".png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesReac);
                reac.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
                reac.renderAll();
            }
        }
        GL11.glPopMatrix();
    }
}
