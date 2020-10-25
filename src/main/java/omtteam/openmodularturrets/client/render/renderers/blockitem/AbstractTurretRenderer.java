package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.client.render.models.ModelAbstractTurret;
import omtteam.openmodularturrets.client.render.models.ModelDamageAmp;
import omtteam.openmodularturrets.client.render.models.ModelRedstoneReactor;
import omtteam.openmodularturrets.client.render.models.ModelSolarPanelAddon;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import org.lwjgl.opengl.GL11;

public abstract class AbstractTurretRenderer extends TileEntitySpecialRenderer {
    protected final ModelSolarPanelAddon solar;
    protected final ModelDamageAmp amp;
    protected final ModelRedstoneReactor reac;

    public AbstractTurretRenderer() {
        this.solar = new ModelSolarPanelAddon();
        this.amp = new ModelDamageAmp();
        this.reac = new ModelRedstoneReactor();
    }

    /**
     * This should give back which addons should be rendered
     *
     * @return least significant bit is damage, next is solar, next is redstone reactor
     */
    protected abstract byte addonsRendered();

    protected void render(TurretHead turretHead, boolean doRotation, ModelAbstractTurret model, double x, double y, double z) {
        int rotation;
        AbstractDirectedTurret directedTurret = null;
        if (doRotation) {
            directedTurret = (AbstractDirectedTurret) turretHead;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        if (turretHead == null) {
            GL11.glScalef(0.7F, -0.7F, -0.7F);
            GL11.glTranslatef((float) x + 0.0F, (float) y + 0.4F, (float) z + 0.5F);
            GL11.glRotatef(45.0F, 2.5F, -4.5F, -1.0F);
            model.setRotationForTarget(0, 0);
            model.renderAll();
            GL11.glPopMatrix();
            return;
        }

        if (turretHead.shouldConceal) {
            GL11.glPopMatrix();
            return;
        }

        if (turretHead.getWorld() != null) {
            rotation = turretHead.getBlockMetadata();
            GL11.glRotatef(rotation * 90, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, -1F, -1F);
            if (doRotation) {
                model.setRotationForTarget(MathUtil.getRotationXYFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()), MathUtil.getRotationXZFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()));
            }
            model.setBaseRotation(turretHead);
        }

        model.renderAll();

        if (turretHead.getBase() != null) {

            if (TurretHeadUtil.getAmpLevel(turretHead.getBase()) > 0 && ((this.addonsRendered()) & 1) != 0) {
                ResourceLocation texturesAmp = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/addon_damage_amp.png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesAmp);
                if (doRotation) {
                    amp.setRotationForTarget(MathUtil.getRotationXYFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()), MathUtil.getRotationXZFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()));
                }
                amp.renderAll();
            }

            if (TurretHeadUtil.hasSolarPanelAddon(turretHead.getBase()) && ((this.addonsRendered() >>> 1) & 1) != 0) {
                ResourceLocation texturesSolar = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/addon_solar_panel.png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesSolar);
                if (doRotation) {
                    solar.setRotationForTarget(MathUtil.getRotationXYFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()), MathUtil.getRotationXZFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()));
                }
                solar.renderAll();
            }

            if (TurretHeadUtil.hasRedstoneReactor(turretHead.getBase()) && ((this.addonsRendered() >>> 2) & 1) != 0) {
                ResourceLocation texturesReac = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/addon_redstone_reactor.png"));
                Minecraft.getMinecraft().renderEngine.bindTexture(texturesReac);
                if (doRotation) {
                    reac.setRotationForTarget(MathUtil.getRotationXYFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()), MathUtil.getRotationXZFromYawPitch(directedTurret.getYaw(), directedTurret.getPitch()));
                }
                reac.renderAll();
            }
        }
        GL11.glPopMatrix();
    }
}
