package omtteam.openmodularturrets.api.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public interface INetworkTile {
    // TODO: write this.

    /**
     * Return, if available, the current network the tile is on.
     *
     * @return the network, or null if not on any network
     */
    @Nullable
    OMTNetwork getNetwork();

    /**
     * Set the currently connected network for the tile. If set to null, refresh on next tick.
     */
    void setNetwork(@Nullable OMTNetwork network);

    /**
     * Return the devices name.
     *
     * @return name of device
     */
    @Nonnull
    String getDeviceName();

    /**
     * Return the position of the tile.
     *
     * @return BlockPos position
     */
    @Nonnull
    BlockPos getPosition();

    default void recursAddDevice(World world, BlockPos pos, @Nullable EnumFacing from) {
        if (world.isBlockLoaded(pos)) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity te = world.getTileEntity(pos.offset(facing));
                if (!facing.equals(from) && te instanceof INetworkTile & te != null) {
                    ((INetworkTile) te).recursAddDevice(world, pos.offset(facing), facing);
                }
            }
        }
    }

    default OMTNetwork createNetwork(World world) {
        OMTNetwork network = new OMTNetwork(world);
        network.addDevice(this);
        recursAddDevice(world, this.getPosition(), null);
        return network;
    }
}
