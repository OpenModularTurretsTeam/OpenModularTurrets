package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelGrenadeLauncher;
import openmodularturrets.client.render.renderers.blockitem.GrenadeLauncherTurretRenderer;
import openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;

class GrenadeLauncherTurretItemRenderer implements IItemRenderer {

    private final GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer;
    private final GrenadeLauncherTurretTileEntity grenadeLauncherTurretTileEntity;
    private final ModelGrenadeLauncher model;

    public GrenadeLauncherTurretItemRenderer(GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer,
            GrenadeLauncherTurretTileEntity grenadeLauncherTurretTileEntity) {
        this.grenadeLauncherTurretRenderer = grenadeLauncherTurretRenderer;
        this.grenadeLauncherTurretTileEntity = grenadeLauncherTurretTileEntity;

        this.model = new ModelGrenadeLauncher();
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
        this.grenadeLauncherTurretRenderer
                .renderTileEntityAt(this.grenadeLauncherTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
