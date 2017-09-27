package omtteam.openmodularturrets.api.network;

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
     * Set the currently connected network for the tile.
     */
    void setNetwork(OMTNetwork network);

    /**
     * Return a list of all networking capable tiles near the cable.
     *
     * @return the list.
     */
    String getDeviceName();
}
