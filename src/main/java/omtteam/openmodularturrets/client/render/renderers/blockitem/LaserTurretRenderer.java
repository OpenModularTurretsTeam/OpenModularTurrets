package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelLaserTurret;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

class LaserTurretRenderer extends AbstractTurretRenderer {
    private final ModelLaserTurret model;

    public LaserTurretRenderer() {
        super();
        model = new ModelLaserTurret();
    }

    @Override
    protected byte addonsRendered() {
        return 0b111;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TurretHead turretHead = (TurretHead) te;
        boolean doRotation = false;
        if (turretHead instanceof AbstractDirectedTurret) {
            doRotation = true;
        }

        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/laser_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        this.render(turretHead, doRotation, model, x, y, z);
    }
}
