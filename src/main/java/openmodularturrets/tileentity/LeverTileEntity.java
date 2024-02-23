package openmodularturrets.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class LeverTileEntity extends TileEntity {

    public float rotation = 0;
    public boolean isTurning = false;
    private TurretBaseTierOneTileEntity base;

    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        super.writeToNBT(var1);

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, var1);
    }

    @Override
    public void updateEntity() {
        if (base == null) {
            if (worldObj.getTileEntity(xCoord + 1, yCoord, zCoord) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
            }

            if (worldObj.getTileEntity(xCoord - 1, yCoord, zCoord) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
            }

            if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 1) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
            }

            if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 1) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
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
        super.writeToNBT(par1);
    }

}
