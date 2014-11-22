package modularTurrets.tileentity.turretBase;

import net.minecraft.item.ItemStack;

public class TurretBaseTierFourTileEntity extends TurretBase {

	public TurretBaseTierFourTileEntity() {
		super();
		inv = new ItemStack[13];
	}

	public TurretBaseTierFourTileEntity(int MaxEnergyStorage, int MaxIO) {
		super(MaxEnergyStorage, MaxIO);
		inv = new ItemStack[13];
		baseTier = 4;
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}

    @Override
    public String getInventoryName() {
        return "modtur.turretbasefour";
    }
}
