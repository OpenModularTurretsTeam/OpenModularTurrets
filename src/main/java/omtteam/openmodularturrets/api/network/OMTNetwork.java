package omtteam.openmodularturrets.api.network;

import jline.internal.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import omtteam.openmodularturrets.api.IBaseController;
import omtteam.openmodularturrets.handler.OMTEventHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.UUID.randomUUID;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public class OMTNetwork {
    private Map<BlockPos, INetworkTile> devices = new HashMap<>();
    private World world;
    private UUID uuid;
    private String name;

    /**
     * This is the default constructor for a network without a custom name.
     * Names can be added to an existing network later.
     *
     * @param world the world the network is in
     */
    public OMTNetwork(World world) {
        this.world = world;
        this.uuid = randomUUID();
        OMTEventHandler.getInstance().registerNetwork(this);
    }

    /**
     * This is the constructor for a network with a give name.
     *
     * @param world the world the network is in
     * @param name  the name of the network
     */
    public OMTNetwork(World world, String name) {
        this.world = world;
        this.uuid = randomUUID();
        this.name = name;
        OMTEventHandler.getInstance().registerNetwork(this);
    }

    /**
     * This is the constructor that is called when the networks are being loaded from disk.
     * Do NOT call this manually anywhere.
     *
     * @param world the world the network is in
     * @param name  the name of the network
     * @param uuid  the uuid of the network
     */
    public OMTNetwork(World world, String name, UUID uuid) {
        this.world = world;
        this.uuid = uuid;
        this.name = name;
        OMTEventHandler.getInstance().registerNetwork(this);
        this.loadFromDisk(uuid, world);
    }

    /**
     * This is the method that searches for energy giving and energy requiring devices and
     * gathers and distributes the energy in the network
     * Do NOT call this manually anywhere.
     */
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

    /**
     * This returns, if existing the TurretBaseController in the network.
     */
    @Nullable
    public IBaseController getController() {
        for (INetworkTile device : devices.values()) {
            if (device instanceof IBaseController) {
                return (IBaseController) device;
            }
        }
        return null;
    }

    /**
     * Use this to add a device to the network.
     *
     * @param tile The tile to be added
     * @return true if possible, false if it failed.
     */
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

    /**
     * Use this to remove a device from the network.
     *
     * @param tile The tile to be added
     * @return true if possible, false if it failed.
     */
    public boolean removeDevice(INetworkTile tile) {
        return this.devices.remove(tile.getPosition()) != null;
    }

    /**
     * Use this to get a device on a certain position from the network.
     *
     * @param pos The BlockPos of the required device
     * @return if loaded and existing, the device, null otherwise
     */
    @Nullable
    public INetworkTile getConnectedDevice(BlockPos pos) {
        return world.isBlockLoaded(pos) ? devices.get(pos) : null;
    }

    /**
     * This gives back a Collection of all the connected devices.
     *
     * @return the collection
     */
    public Collection<INetworkTile> getAllDevices() {
        return devices.values();
    }

    // Getters and Setters

    public World getWorld() {
        return world;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Utility Functions

    private boolean isSplitted() {
        HashMap<BlockPos, INetworkTile> tempmap = new HashMap<>();
        recursiveSearch((BlockPos) devices.keySet().toArray()[0], null, tempmap);
        return tempmap.keySet().equals(devices.keySet());
    }

    private void recursiveSearch(BlockPos pos, @Nullable EnumFacing from, HashMap<BlockPos, INetworkTile> tempmap) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (!facing.equals(from) && te instanceof INetworkTile & te != null) {
                tempmap.put(pos.offset(facing), (INetworkTile) te);
                recursiveSearch(pos.offset(facing), facing.getOpposite(), tempmap);
            }
        }
    }

    public void splitNetwork() {
        // set all networktile to refresh their network.
        if (isSplitted()) {
            for (INetworkTile tile : devices.values()) {
                tile.setNetwork(null);
            }
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

    public void saveToDisk() {
        File saveRoot = DimensionManager.getCurrentSaveRootDirectory();
        if (saveRoot != null) {
            Path path = Paths.get(saveRoot.toString() + "/omt/");
            Path fullpath = Paths.get(saveRoot.toString() + "/omt/network-" + this.getUuid().toString() + ".sav");
            try {
                if (Files.notExists(path)) {
                    if (!path.toFile().mkdir()) {
                        throw new Exception("Failed to create dir");
                    }
                }
                FileOutputStream saveFile = new FileOutputStream(fullpath.toFile());
                ObjectOutputStream save = new ObjectOutputStream(saveFile);
                save.writeObject(this.devices.keySet());
                save.close();
                saveFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Files.deleteIfExists(fullpath);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void loadFromDisk(UUID uuid, World world) {
        try {
            Set<BlockPos> tempList = new HashSet<>();
            Path fullpath = Paths.get(DimensionManager.getCurrentSaveRootDirectory().toString() + "/omt/network- " + uuid.toString() + ").sav");
            FileInputStream saveFile = new FileInputStream(fullpath.toFile());
            ObjectInputStream save = new ObjectInputStream(saveFile);
            Object object = save.readObject();
            if (object instanceof Set) {
                tempList = (HashSet<BlockPos>) object;
            }
            save.close();
            saveFile.close();
            for (BlockPos pos : tempList) {
                devices.put(pos, (INetworkTile) world.getTileEntity(pos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
