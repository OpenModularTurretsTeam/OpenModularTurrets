package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelIncendiaryTurret;
import openmodularturrets.client.render.renderers.blockitem.IncendiaryTurretRenderer;
import openmodularturrets.tileentity.turrets.IncendiaryTurretTileEntity;

class IncendiaryTurretItemRenderer implements IItemRenderer {

    private final IncendiaryTurretRenderer incendiaryTurretRenderer;
    private final IncendiaryTurretTileEntity incendiaryTurretTileEntity;
    private final ModelIncendiaryTurret model;

    public IncendiaryTurretItemRenderer(IncendiaryTurretRenderer incendiaryTurretRenderer,
            IncendiaryTurretTileEntity incendiaryTurretTileEntity) {
        this.incendiaryTurretRenderer = incendiaryTurretRenderer;
        this.incendiaryTurretTileEntity = incendiaryTurretTileEntity;

        this.model = new ModelIncendiaryTurret();
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
        this.incendiaryTurretRenderer.renderTileEntityAt(this.incendiaryTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
