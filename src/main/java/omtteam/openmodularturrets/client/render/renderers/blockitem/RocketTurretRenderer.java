package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelDamageAmp;
import omtteam.openmodularturrets.client.render.models.ModelRedstoneReactor;
import omtteam.openmodularturrets.client.render.models.ModelRocketTurret;
import omtteam.openmodularturrets.client.render.models.ModelSolarPanelAddon;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import org.lwjgl.opengl.GL11;

public class RocketTurretRenderer extends TileEntitySpecialRenderer {
    private final ModelSolarPanelAddon solar;
    private final ModelDamageAmp amp;
    private final ModelRedstoneReactor reac;
    private final ModelRocketTurret model;

    public RocketTurretRenderer() {
        model = new ModelRocketTurret();
        solar = new ModelSolarPanelAddon();
        amp = new ModelDamageAmp();
        reac = new ModelRedstoneReactor();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale, int destroyStage)  {
        TurretHead turretHead = (TurretHead) te;

        if (turretHead.shouldConceal) {
            return;
        }

        this.model.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/rocket_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);

        model.Base.rotateAngleX = turretHead.baseFitRotationX;
        model.Base.rotateAngleY = turretHead.baseFitRotationZ;
        model.Pole.rotateAngleX = turretHead.baseFitRotationX;
        model.Pole.rotateAngleY = turretHead.baseFitRotationZ;
        model.BoxUnder.rotateAngleX = turretHead.baseFitRotationX;
        model.renderAll();

        if (turretHead.base != null) {
            if (TurretHeadUtil.hasSolarPanelAddon(turretHead.base)) {
                ResourceLocation texturesSolar = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/addon_solar_panel.png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesSolar);
                solar.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
                solar.renderAll();
            }

            if (TurretHeadUtil.hasDamageAmpAddon(turretHead.base)) {
                ResourceLocation texturesAmp = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/addon_damage_amp.png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesAmp);
                amp.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
                amp.renderAll();
            }

            if (TurretHeadUtil.hasRedstoneReactor(turretHead.base)) {
                ResourceLocation texturesReac = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/redstone_reactor.png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesReac);
                reac.setRotationForTarget(turretHead.rotationXY, turretHead.rotationXZ);
                reac.renderAll();
            }
        }
        GL11.glPopMatrix();
    }
}
