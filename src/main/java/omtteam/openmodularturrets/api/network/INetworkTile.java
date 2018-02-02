package omtteam.openmodularturrets.api.network;

import net.minecraft.util.math.BlockPos;

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
}
