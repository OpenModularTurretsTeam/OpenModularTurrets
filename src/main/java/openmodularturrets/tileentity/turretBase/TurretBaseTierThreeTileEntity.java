package openmodularturrets.tileentity.turretBase;

import openmodularturrets.misc.ConfigHandler;

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
}
