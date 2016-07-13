package api.undercurrent.iface;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public class UCTileDefinition  {

    ArrayList<UCCollection> collections;

    public UCTileDefinition() {
        collections = new ArrayList<UCCollection>();
    }

    public ArrayList<UCCollection> getCollections() {
        return collections;
    }
}
