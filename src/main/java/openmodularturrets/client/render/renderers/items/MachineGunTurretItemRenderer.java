package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelMachineGun;
import openmodularturrets.client.render.renderers.blockitem.MachineGunTurretRenderer;
import openmodularturrets.tileentity.turrets.GunTurretTileEntity;

class MachineGunTurretItemRenderer implements IItemRenderer {

    private final MachineGunTurretRenderer machineGunTurretRenderer;
    private final GunTurretTileEntity gunTurretTileEntity;
    private final ModelMachineGun model;

    public MachineGunTurretItemRenderer(MachineGunTurretRenderer machineGunTurretRenderer,
            GunTurretTileEntity gunTurretTileEntity) {
        this.machineGunTurretRenderer = machineGunTurretRenderer;
        this.gunTurretTileEntity = gunTurretTileEntity;

        this.model = new ModelMachineGun();
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
        this.machineGunTurretRenderer.renderTileEntityAt(this.gunTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
