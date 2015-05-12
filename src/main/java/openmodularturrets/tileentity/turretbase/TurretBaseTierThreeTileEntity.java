package openmodularturrets.tileentity.turretbase;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import openmodularturrets.handler.ConfigHandler;

public class TurretBaseTierThreeTileEntity extends TurretBase {

    public TurretBaseTierThreeTileEntity() {
        this(ConfigHandler.getBaseTierThreeMaxCharge(), ConfigHandler.getBaseTierThreeMaxIo());
    }

    public TurretBaseTierThreeTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 3;
    }

    @Override
    public int getSizeInventory() {
        return 12;
    }
    
    @Override
    public String getInventoryName() {
        return "modtur.turretbasethree";
    }
    @Optional.Method(modid = "OpenComputers")
	@Override
	public String getComponentName() {
		return "hardened_base";
	}
}
