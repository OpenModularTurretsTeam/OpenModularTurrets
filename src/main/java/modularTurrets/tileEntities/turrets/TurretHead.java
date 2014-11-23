package modularTurrets.tileEntities.turrets;

import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turretBase.TurretWoodBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

public class TurretHead extends TileEntity {

    public Entity target;
    int ticks;
    public float rotationXY;
    public float rotationXZ;
    public float baseFitRotationX;
    public float baseFitRotationZ;
    public int turretTier;
    public TurretBase base;
    public boolean hasSetSide = false;
    
    public TurretHead() {
	super();
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
    public void writeToNBT(NBTTagCompound par1) {
	par1.setFloat("rotationXY", rotationXY);
	par1.setFloat("rotationXZ", rotationXZ);
	par1.setInteger("ticksBeforeFire", ticks);
	super.writeToNBT(par1);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
	super.readFromNBT(par1);
	this.rotationXY = par1.getFloat("rotationXY");
	this.rotationXZ = par1.getFloat("rotationXZ");
	this.ticks = par1.getInteger("ticksBeforeFire");
    }

    public Entity getTarget() {
	return target;
    }

    public void setTarget(Entity target) {
	this.target = target;
    }

    public int getTicks() {
	return ticks;
    }

    public void setTicks(int ticks) {
	this.ticks = ticks;
    }

    public float getRotationXY() {
	return rotationXY;
    }

    public void setRotationXY(float rotationXY) {
	this.rotationXY = rotationXY;
    }

    public float getRotationXZ() {
	return rotationXZ;
    }

    public void setRotationXZ(float rotationXZ) {
	this.rotationXZ = rotationXZ;
    }

}
