package openmodularturrets.tileentity.turretbase;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import openmodularturrets.handler.ConfigHandler;

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
        return 13;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasefour";
    }
    @Optional.Method(modid = "OpenComputers")
	@Override
	public String getComponentName() {
		return "resonant_base";
	}
}
