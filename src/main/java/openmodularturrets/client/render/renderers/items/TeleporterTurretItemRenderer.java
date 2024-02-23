package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelTeleporterTurret;
import openmodularturrets.client.render.renderers.blockitem.TeleporterTurretRenderer;
import openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

class TeleporterTurretItemRenderer implements IItemRenderer {

    private final TeleporterTurretRenderer teleporterTurretRenderer;
    private final TeleporterTurretTileEntity teleporterTurretTileEntity;
    private final ModelTeleporterTurret model;

    public TeleporterTurretItemRenderer(TeleporterTurretRenderer teleporterTurretRenderer,
            TeleporterTurretTileEntity teleporterTurretTileEntity) {
        this.teleporterTurretRenderer = teleporterTurretRenderer;
        this.teleporterTurretTileEntity = teleporterTurretTileEntity;

        this.model = new ModelTeleporterTurret();
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
        this.teleporterTurretRenderer.renderTileEntityAt(this.teleporterTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
