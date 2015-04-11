package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class ConfigContainer extends Container {

    protected TurretBase tileEntity;

    public ConfigContainer(InventoryPlayer inventoryPlayer,
                           TurretBase te) {
        this.tileEntity = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}