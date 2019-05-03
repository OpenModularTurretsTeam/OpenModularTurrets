package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelRelativisticTurret;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

class RelativisticTurretRenderer extends AbstractTurretRenderer {
    private final ModelRelativisticTurret model;

    public RelativisticTurretRenderer() {
        super();
        model = new ModelRelativisticTurret();
    }

    @Override
    protected byte addonsRendered() {
        return 0b010;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TurretHead turretHead = (TurretHead) te;
        boolean doRotation = false;
        if (turretHead instanceof AbstractDirectedTurret) {
            doRotation = true;
        }

        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/relativistic_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        this.render(turretHead, doRotation, model, x, y, z);
    }
}
