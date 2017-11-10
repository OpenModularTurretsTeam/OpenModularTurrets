package omtteam.openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import omtteam.omlib.util.DebugHandler;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.network.messages.MessageTurretBase;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;

public class ConfigContainer extends Container {
    private final TurretBase tileEntity;

    public ConfigContainer(TurretBase te, EntityPlayer player) {
        this.tileEntity = te;
        DebugHandler.getInstance().setPlayer(player);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUsableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        DebugHandler.getInstance().setListeners(this.listeners);
        for (IContainerListener listener : this.listeners) {
            if (listener instanceof EntityPlayerMP) {
                NetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this.tileEntity), (EntityPlayerMP) listener);
            }
        }
    }
}