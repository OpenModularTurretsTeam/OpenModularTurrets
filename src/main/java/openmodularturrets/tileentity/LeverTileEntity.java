package openmodularturrets.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class LeverTileEntity extends TileEntity implements ITickable {
    public float rotation = 0;
    public boolean isTurning = false;
    private TurretBaseTierOneTileEntity base;

    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        super.writeToNBT(var1);

        return new S35PacketUpdateTileEntity(this.pos, 2, var1);
    }

    @Override
    public void update() {
        if (base == null) {
            if (worldObj.getTileEntity(this.pos.east()) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(this.pos.east());
            }

            if (worldObj.getTileEntity(this.pos.west()) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(this.pos.west());
            }

            if (worldObj.getTileEntity(this.pos.south()) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(this.pos.south());
            }

            if (worldObj.getTileEntity(this.pos.north()) instanceof TurretBaseTierOneTileEntity) {
                this.base = (TurretBaseTierOneTileEntity) worldObj.getTileEntity(this.pos.north());
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
