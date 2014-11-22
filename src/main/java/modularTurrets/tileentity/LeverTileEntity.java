package modularTurrets.tileentity;

import modularTurrets.tileentity.turretBase.TurretWoodBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class LeverTileEntity extends TileEntity {

    TurretWoodBase base;
    public float rotation = 0;
    public boolean isTurning = false;

    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, var1);
    }

    @Override
    public void updateEntity() {
        if (base == null) {
            if (worldObj.getTileEntity(xCoord + 1, yCoord, zCoord) instanceof TurretWoodBase) {
                this.base = (TurretWoodBase) worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
            }

            if (worldObj.getTileEntity(xCoord - 1, yCoord, zCoord) instanceof TurretWoodBase) {
                this.base = (TurretWoodBase) worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
            }

            if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 1) instanceof TurretWoodBase) {
                this.base = (TurretWoodBase) worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
            }

            if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 1) instanceof TurretWoodBase) {
                this.base = (TurretWoodBase) worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
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
        this.base = (TurretWoodBase) worldObj.getTileEntity(
                par1.getInteger("baseXCoord"),
                par1.getInteger("baseYCoord"),
                par1.getInteger("baseXCoord")
        );

        super.readFromNBT(par1);
    }
}
