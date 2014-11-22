package modularTurrets.tileentity.turretBase;

import net.minecraft.item.ItemStack;

public class TurretBaseTierThreeTileEntity extends TurretBase {

	public TurretBaseTierThreeTileEntity() {
		super();
		inv = new ItemStack[12];
	}

	public TurretBaseTierThreeTileEntity(int MaxEnergyStorage, int MaxIO) {
		super(MaxEnergyStorage, MaxIO);
		inv = new ItemStack[12];
		baseTier = 3;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	 @Override
	 public String getInventoryName() {
		return "modtur.turretbasethree";
	 }
}
