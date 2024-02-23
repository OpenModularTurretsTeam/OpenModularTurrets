package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelRocketTurret;
import openmodularturrets.client.render.renderers.blockitem.RocketTurretRenderer;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;

class RocketTurretItemRenderer implements IItemRenderer {

    private final RocketTurretRenderer rocketTurretRenderer;
    private final RocketTurretTileEntity rocketTurretTileEntity;
    private final ModelRocketTurret model;

    public RocketTurretItemRenderer(RocketTurretRenderer rocketTurretRenderer,
            RocketTurretTileEntity rocketTurretTileEntity) {
        this.rocketTurretRenderer = rocketTurretRenderer;
        this.rocketTurretTileEntity = rocketTurretTileEntity;

        this.model = new ModelRocketTurret();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glTranslated(-0.5, -0.5, -0.5);
        this.rocketTurretRenderer.renderTileEntityAt(this.rocketTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
