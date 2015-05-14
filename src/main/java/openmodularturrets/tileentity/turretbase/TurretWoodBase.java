package openmodularturrets.tileentity.turretbase;

import cpw.mods.fml.common.Optional;
import openmodularturrets.handler.ConfigHandler;

public class TurretWoodBase extends TurretBase {

    public TurretWoodBase() {
        this(ConfigHandler.getBaseTierWoodMaxCharge(), ConfigHandler.getBaseTierWoodMaxIo());
    }

    public TurretWoodBase(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 0;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasewood";
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "wood_base";
    }
}
