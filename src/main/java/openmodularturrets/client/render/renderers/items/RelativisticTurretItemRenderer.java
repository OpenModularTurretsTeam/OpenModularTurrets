package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelRelativisticTurret;
import openmodularturrets.client.render.renderers.blockitem.RelativisticTurretRenderer;
import openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;

class RelativisticTurretItemRenderer implements IItemRenderer {

    private final RelativisticTurretRenderer relativisticTurretRenderer;
    private final RelativisticTurretTileEntity relativisticTurretTileEntity;
    private final ModelRelativisticTurret model;

    public RelativisticTurretItemRenderer(RelativisticTurretRenderer relativisticTurretRenderer,
            RelativisticTurretTileEntity relativisticTurretTileEntity) {
        this.relativisticTurretRenderer = relativisticTurretRenderer;
        this.relativisticTurretTileEntity = relativisticTurretTileEntity;

        this.model = new ModelRelativisticTurret();
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
        this.relativisticTurretRenderer.renderTileEntityAt(this.relativisticTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
