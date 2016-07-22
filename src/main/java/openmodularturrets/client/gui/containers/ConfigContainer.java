package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import openmodularturrets.tileentity.TurretBase;

public class ConfigContainer extends Container {
    private final TurretBase tileEntity;

    public ConfigContainer(TurretBase te) {
        this.tileEntity = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}