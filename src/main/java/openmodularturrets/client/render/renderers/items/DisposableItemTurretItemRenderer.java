package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelDisposableItemTurret;
import openmodularturrets.client.render.renderers.blockitem.DisposableItemTurretRenderer;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;

class DisposableItemTurretItemRenderer implements IItemRenderer {

    private final DisposableItemTurretRenderer disposableItemTurretRenderer;
    private final DisposableItemTurretTileEntity disposableItemTurretTileEntity;
    private final ModelDisposableItemTurret model;

    public DisposableItemTurretItemRenderer(DisposableItemTurretRenderer disposableItemTurretRenderer,
            DisposableItemTurretTileEntity disposableItemTurretTileEntity) {
        this.disposableItemTurretRenderer = disposableItemTurretRenderer;
        this.disposableItemTurretTileEntity = disposableItemTurretTileEntity;

        this.model = new ModelDisposableItemTurret();
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
        this.disposableItemTurretRenderer
                .renderTileEntityAt(this.disposableItemTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
