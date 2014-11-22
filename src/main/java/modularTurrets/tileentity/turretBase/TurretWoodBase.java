package modularTurrets.tileentity.turretBase;

import net.minecraft.item.ItemStack;

public class TurretWoodBase extends TurretBase {

	public TurretWoodBase() {
		super();
		inv = new ItemStack[9];
	}

	public TurretWoodBase(int MaxEnergyStorage, int MaxIO) {
		super(MaxEnergyStorage, MaxIO);
		inv = new ItemStack[9];
		baseTier = 0;
	}

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasewood";
    }
}
