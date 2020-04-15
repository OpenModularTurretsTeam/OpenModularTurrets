package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelGrenadeLauncher;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

class PlasmaLauncherTurretRenderer extends AbstractTurretRenderer {
    private final ModelGrenadeLauncher model;

    public PlasmaLauncherTurretRenderer() {
        super();
        model = new ModelGrenadeLauncher();
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

        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/grenade_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        this.render(turretHead, doRotation, model, x, y, z);
    }
}
