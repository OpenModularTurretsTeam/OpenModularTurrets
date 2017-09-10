package omtteam.openmodularturrets.api.network;

import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public interface INetworkCable {

    /**
     * Return true if the cable should connect to the given side.
     *
     * @param side side to check connection state to
     * @return connection state.
     */
    boolean shouldConnect(EnumFacing side);

    /**
     * Return a list of all networking capable tiles touching the cable.
     *
     * @return the list.
     */
    List<INetworkTile> getConnectedDevices();

    /**
     * Return a list of all networking capable tiles near the cable.
     *
     * @return the list.
     */
    OMTNetwork getConnectedNetwork();
}
