package modularTurrets.tileEntities.turretBase;

import net.minecraft.item.ItemStack;

public class TurretBaseTierOneTileEntity extends TurretBase {

	public TurretBaseTierOneTileEntity() {
		super();
		inv = new ItemStack[12];
	}

	public TurretBaseTierOneTileEntity(int MaxEnergyStorage, int MaxIO) {
		super(MaxEnergyStorage, MaxIO);
		inv = new ItemStack[12];
		baseTier = 1;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}
	
	 @Override
	    public String getInvName() {
		return "modtur.turretbaseone";
	    }

}
