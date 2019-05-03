package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.client.render.models.ModelDisposableItemTurret;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

class DisposableItemTurretRenderer extends AbstractTurretRenderer {
    private final ModelDisposableItemTurret model;

    public DisposableItemTurretRenderer() {
        super();
        model = new ModelDisposableItemTurret();
    }

    @Override
    protected byte addonsRendered() {
        return 0b111;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @SideOnly(Side.CLIENT)
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TurretHead turretHead = (TurretHead) te;
        boolean doRotation = false;
        if (turretHead instanceof AbstractDirectedTurret) {
            doRotation = true;
        }

        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/dispose_item_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        this.render(turretHead, doRotation, model, x, y, z);
    }
}
