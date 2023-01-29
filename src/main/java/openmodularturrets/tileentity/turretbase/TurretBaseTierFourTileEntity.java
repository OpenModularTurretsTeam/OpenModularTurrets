package openmodularturrets.tileentity.turretbase;

import openmodularturrets.handler.ConfigHandler;
import cpw.mods.fml.common.Optional;

public class TurretBaseTierFourTileEntity extends TurretBase {

    public TurretBaseTierFourTileEntity() {
        this(ConfigHandler.getBaseTierFourMaxCharge(), ConfigHandler.getBaseTierFourMaxIo());
    }

    public TurretBaseTierFourTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 4;
    }

    @Override
    public int getSizeInventory() {
        return 12;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasefour";
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierFourTurretBase";
    }
}
