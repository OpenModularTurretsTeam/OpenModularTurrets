package omtteam.openmodularturrets.api.network;

import jline.internal.Nullable;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.IBaseController;
import omtteam.openmodularturrets.handler.OMTEventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public class OMTNetwork {
    private List<INetworkTile> devices = new ArrayList<>();
    private World world;

    public OMTNetwork(World world) {
        this.world = world;
        OMTEventHandler.getInstance().registerNetwork(this);
    }

    public void tick() {
        List<IPowerExchangeTile> delivering = new ArrayList<>();
        List<IPowerExchangeTile> requiring = new ArrayList<>();
        int powerRequired = 0, powerToDeliver = 0;

        for (INetworkTile device : devices) {
            if (device instanceof IPowerExchangeTile) {
                if (((IPowerExchangeTile) device).deliversEnergy()) {
                    delivering.add((IPowerExchangeTile) device);
                } else if (((IPowerExchangeTile) device).requiresEnergy()) {
                    requiring.add((IPowerExchangeTile) device);
                }
            }
        }
        for (IPowerExchangeTile tile : requiring) {
            powerRequired += Math.min(tile.getEnergyStorage().getMaxReceive(),
                    tile.getEnergyStorage().getMaxEnergyStored() - tile.getEnergyStorage().getEnergyStored());
        }
        for (IPowerExchangeTile tile : delivering) {
            powerToDeliver += Math.min(powerRequired - powerToDeliver, Math.min(tile.getEnergyStorage().getMaxExtract(),
                    tile.getEnergyStorage().getMaxEnergyStored() - tile.getEnergyStorage().getEnergyStored()));
        }
        for (IPowerExchangeTile tile : requiring) {
            tile.getEnergyStorage().receiveEnergy(powerToDeliver / requiring.size(), false);
            powerToDeliver -= powerToDeliver / requiring.size();
        }
    }

    @Nullable
    public IBaseController getController() {
        for (INetworkTile device : devices) {
            if (device instanceof IBaseController) {
                return (IBaseController) device;
            }
        } 
        return null;
    }

    public boolean addDevice(INetworkTile tile) {
        boolean controllerExists = getController() != null;
        if (tile instanceof IBaseController && !controllerExists) {
            this.devices.add(tile);
            return true;
        } else if (!(tile instanceof IBaseController)) {
            this.devices.add(tile);
            return true;
        }
        return false;
    }

    //TODO: add merging and splitting function

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
