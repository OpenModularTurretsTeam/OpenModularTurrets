package omtteam.openmodularturrets.api.network;

import omtteam.omlib.power.OMEnergyStorage;

/**
 * Created by Keridos on 30/08/17.
 * This Class
 */
public interface IPowerExchangeTile extends INetworkTile {

    boolean requiresEnergy();

    boolean deliversEnergy();

    OMEnergyStorage getEnergyStorage();

    //TODO: javadoc
}
