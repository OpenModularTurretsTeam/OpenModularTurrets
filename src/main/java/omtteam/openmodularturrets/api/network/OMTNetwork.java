package omtteam.openmodularturrets.api.network;

import jdk.nashorn.internal.ir.Block;
import jline.internal.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.IBaseController;
import omtteam.openmodularturrets.handler.OMTEventHandler;

import java.io.Serializable;
import java.util.*;

import static java.util.UUID.randomUUID;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public class OMTNetwork implements Serializable {
    private Map<BlockPos, INetworkTile> devices = new HashMap<>();
    private transient World world;
    private UUID uuid;
    private String name;

    public OMTNetwork(World world) {
        this.world = world;
        this.uuid = randomUUID();
        OMTEventHandler.getInstance().registerNetwork(this);
    }

    public OMTNetwork(World world, String name) {
        this.world = world;
        this.uuid = randomUUID();
        this.name = name;
        OMTEventHandler.getInstance().registerNetwork(this);
    }

    public void tick() {
        List<IPowerExchangeTile> delivering = new ArrayList<>();
        List<IPowerExchangeTile> requiring = new ArrayList<>();
        int powerRequired = 0, powerToDeliver = 0;

        for (Map.Entry<BlockPos, INetworkTile> device : devices.entrySet()) {
            if (world.isBlockLoaded(device.getKey()) && device.getValue() instanceof IPowerExchangeTile) {
                if (((IPowerExchangeTile) device.getValue()).deliversEnergy()) {
                    delivering.add((IPowerExchangeTile) device.getValue());
                } else if (((IPowerExchangeTile) device.getValue()).requiresEnergy()) {
                    requiring.add((IPowerExchangeTile) device.getValue());
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
        return world.isBlockLoaded(pos) ? devices.get(pos) : null;
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

    public NBTTagCompound getAsNBTTagCompound() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("uuid", this.uuid);
        tag.setString("name", this.name);
        return tag;
    }

    public UUID getUuidFromTagCompound(NBTTagCompound tag) {
        return tag.getUniqueId("uuid");
    }

    private void destroy() {
        OMTEventHandler.getInstance().removeNetwork(this);
        this.devices = null;
        this.world = null;
    }

    //TODO: add saving and loading function
}
