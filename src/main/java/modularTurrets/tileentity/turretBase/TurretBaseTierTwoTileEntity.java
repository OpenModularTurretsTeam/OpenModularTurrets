package modularTurrets.tileentity.turretBase;

import net.minecraft.item.ItemStack;

public class TurretBaseTierTwoTileEntity extends TurretBase {

    public TurretBaseTierTwoTileEntity() {
        super();
        inv = new ItemStack[12];
    }

    public TurretBaseTierTwoTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
        inv = new ItemStack[12];
        baseTier = 2;
    }

    @Override
    public int getSizeInventory() {
	return inv.length;
    }

    @Override
    public String getInventoryName() {
	    return "modtur.turretbasetwo";
    }
}
