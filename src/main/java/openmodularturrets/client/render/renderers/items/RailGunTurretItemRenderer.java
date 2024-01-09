package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelLaserTurret;
import openmodularturrets.client.render.renderers.blockitem.RailGunTurretRenderer;
import openmodularturrets.tileentity.turrets.RailGunTurretTileEntity;

class RailGunTurretItemRenderer implements IItemRenderer {

    private final RailGunTurretRenderer railGunTurretRenderer;
    private final RailGunTurretTileEntity railGunTurretTileEntity;
    private final ModelLaserTurret model;

    public RailGunTurretItemRenderer(RailGunTurretRenderer railGunTurretRenderer,
            RailGunTurretTileEntity railGunTurretTileEntity) {
        this.railGunTurretRenderer = railGunTurretRenderer;
        this.railGunTurretTileEntity = railGunTurretTileEntity;

        this.model = new ModelLaserTurret();
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
        this.railGunTurretRenderer.renderTileEntityAt(this.railGunTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
