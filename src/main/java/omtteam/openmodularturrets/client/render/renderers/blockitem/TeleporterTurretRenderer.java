package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelTeleporterTurret;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

class TeleporterTurretRenderer extends AbstractTurretRenderer {
    private final ModelTeleporterTurret model;

    public TeleporterTurretRenderer() {
        super();
        model = new ModelTeleporterTurret();
    }

    @Override
    protected byte addonsRendered() {
        return 0b000;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TurretHead turretHead = (TurretHead) te;
        boolean doRotation = turretHead instanceof AbstractDirectedTurret;

        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/teleporter_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        this.render(turretHead, doRotation, model, x, y, z);
    }
}
