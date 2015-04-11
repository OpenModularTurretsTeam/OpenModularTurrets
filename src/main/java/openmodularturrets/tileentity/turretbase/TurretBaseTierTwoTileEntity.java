package openmodularturrets.tileentity.turretbase;

import openmodularturrets.handler.ConfigHandler;

public class TurretBaseTierTwoTileEntity extends TurretBase {

    public TurretBaseTierTwoTileEntity() {
        this(ConfigHandler.getBaseTierTwoMaxCharge(), ConfigHandler.getBaseTierTwoMaxIo());
    }

    public TurretBaseTierTwoTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public int getSizeInventory() {
        return 12;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasetwo";
    }
}
