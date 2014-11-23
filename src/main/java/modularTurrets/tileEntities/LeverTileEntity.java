package modularTurrets.tileEntities;

import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turretBase.TurretWoodBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class LeverTileEntity extends TileEntity {

    TurretWoodBase base;
    public float rotation = 0;
    public boolean isTurning = false;

    public LeverTileEntity() {
    }

    public Packet getDescriptionPacket() {
	NBTTagCompound var1 = new NBTTagCompound();
	this.writeToNBT(var1);
	return new Packet132TileEntityData(this.xCoord, this.yCoord,
		this.zCoord, 2, var1);
    }

    @Override
    public void onDataPacket(INetworkManager netManager,
	    Packet132TileEntityData packet) {
	readFromNBT(packet.data);
	this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity() {

	if (base == null) {
	    if (worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord) instanceof TurretWoodBase) {
		this.base = (TurretWoodBase) worldObj.getBlockTileEntity(
			xCoord + 1, yCoord, zCoord);
	    }

	    if (worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord) instanceof TurretWoodBase) {
		this.base = (TurretWoodBase) worldObj.getBlockTileEntity(
			xCoord - 1, yCoord, zCoord);
	    }

	    if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1) instanceof TurretWoodBase) {
		this.base = (TurretWoodBase) worldObj.getBlockTileEntity(
			xCoord, yCoord, zCoord + 1);
	    }

	    if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1) instanceof TurretWoodBase) {
		this.base = (TurretWoodBase) worldObj.getBlockTileEntity(
			xCoord, yCoord, zCoord - 1);
	    }
	}
	
	
	if (base != null && isTurning) {
	    rotation = rotation + 30F;
	    if (rotation >= 360F) {
		this.isTurning = false;
		this.rotation = 0F;
	    }
	}
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
	if (base != null) {
	    par1.setInteger("baseXCoord", base.xCoord);
	    par1.setInteger("baseYCoord", base.yCoord);
	    par1.setInteger("baseZCoord", base.zCoord);
	}
	super.writeToNBT(par1);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
	this.base = (TurretWoodBase) worldObj.getBlockTileEntity(
		par1.getInteger("baseXCoord"), par1.getInteger("baseYCoord"),
		par1.getInteger("baseXCoord"));
	super.readFromNBT(par1);
    }
}
