package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import omtteam.openmodularturrets.client.render.models.ModelAbstractTurret;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import org.lwjgl.opengl.GL11;

public abstract class AbstractTurretRenderer extends TileEntitySpecialRenderer {


    public AbstractTurretRenderer() {

    }

    /**
     * This should give back which addons should be rendered
     *
     * @return least significant bit is damage, next is solar, next is redstone reactor
     */
    protected abstract byte addonsRendered();

    protected void render(TurretHead turretHead, boolean doRotation, ModelAbstractTurret model, double x, double y, double z) {
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

        GL11.glScalef(1.0F, -1F, -1F);
        model.setBaseRotation(turretHead);
        if (doRotation) {
            model.setRotationForTarget(TurretHeadUtil.getRotationXYFromTurretHead(directedTurret),
                    TurretHeadUtil.getRotationXZFromTurretHead(directedTurret));
        }

        model.renderAll();
        GL11.glPopMatrix();
    }
}
