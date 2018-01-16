package omtteam.openmodularturrets.api.network;

import jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.IBaseController;
import omtteam.openmodularturrets.handler.OMTEventHandler;

import java.util.*;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public class OMTNetwork {
    private Map<BlockPos, INetworkTile> devices = new HashMap<>();
    private World world;

    public OMTNetwork(World world) {
        this.world = world;
        OMTEventHandler.getInstance().registerNetwork(this);
    }

    public void tick() {
        List<IPowerExchangeTile> delivering = new ArrayList<>();
        List<IPowerExchangeTile> requiring = new ArrayList<>();
        int powerRequired = 0, powerToDeliver = 0;

        for (INetworkTile device : devices.values()) {
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
        for (INetworkTile device : devices.values()) {
            if (device instanceof IBaseController) {
                return (IBaseController) device;
            }
        }
        return null;
    }

    public boolean addDevice(INetworkTile tile) {
        boolean controllerExists = getController() != null;
        if (tile instanceof IBaseController && !controllerExists) {
            this.devices.putIfAbsent(tile.getPosition(), tile);
            return true;
        } else if (!(tile instanceof IBaseController)) {
            this.devices.putIfAbsent(tile.getPosition(), tile);
            return true;
        }
        return false;
    }

    public boolean removeDevice(INetworkTile tile) {
        return this.devices.remove(tile.getPosition()) != null;
    }

    @Nullable
    public INetworkTile getConnectedDevice(BlockPos pos) {
        return devices.get(pos);
    }

    public Collection<INetworkTile> getAllDevices() {
        return devices.values();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void splitNetwork() {
        // set all networktile to refresh their network.
        for (INetworkTile tile : devices.values()) {
            tile.setNetwork(null);
        }
    }

    public void mergeNetwork(OMTNetwork network) {
        // add all the devices from other network to this.
        for (INetworkTile tile : network.getAllDevices()) {
            devices.putIfAbsent(tile.getPosition(), tile);
            tile.setNetwork(this);
        }
        network.destroy();
    }

    private void destroy() {
        OMTEventHandler.getInstance().removeNetwork(this);
        this.devices = null;
        this.world = null;
    }
}
