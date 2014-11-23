package modularTurrets.tileEntities.turretBase;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

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
	public boolean canInterface(ForgeDirection from) {
		if (from == ForgeDirection.UNKNOWN) {
			return true;
		}
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}
	
	  @Override
	    public String getInvName() {
		return "modtur.turretbasewood";
	    }
}
