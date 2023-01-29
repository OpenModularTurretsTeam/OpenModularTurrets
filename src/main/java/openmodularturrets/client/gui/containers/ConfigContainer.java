package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.network.messages.MessageTurretBase;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class ConfigContainer extends Container {

    private final TurretBase tileEntity;

    public ConfigContainer(TurretBase te) {
        this.tileEntity = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : crafters) {
            if (crafter instanceof EntityPlayerMP) {
                NetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this.tileEntity), (EntityPlayerMP) crafter);
            }
        }
    }
}
