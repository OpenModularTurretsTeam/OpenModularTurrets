package openmodularturrets.tileentity;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import openmodularturrets.util.TurretHeadUtil;

import static openmodularturrets.util.MathUtil.truncateDoubleToInt;

public class Expander extends TileEntityContainer implements ITickable {
    public float baseFitRotationX;
    public float baseFitRotationZ;
    protected TurretBase base;
    private boolean hasSetSide = false;
    private boolean powerExpander;

    public Expander() {
        this.inv = new ItemStack[9];
    }

    public Expander(int tier, boolean powerExpander) {
        this.inv = new ItemStack[9];
        this.tier = tier;
        this.powerExpander = powerExpander;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("powerExpander", powerExpander);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.powerExpander= nbtTagCompound.getBoolean("powerExpander");
    }

    @Override
    public int getInventoryStackLimit() {
        return truncateDoubleToInt(Math.pow(2,tier+1));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new S35PacketUpdateTileEntity(this.pos, 2, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound var1 = pkt.getNbtCompound();
        readFromNBT(var1);
    }

    private void setSide() {
        if (worldObj.getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 4.705F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 1.56F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.baseFitRotationX = 4.705F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
        }
    }

    @Override
    public void update() {
        if (!hasSetSide) {
            setSide();
        }

        if (getBase() == null) {
            this.getWorld().destroyBlock(this.pos, true);
        }
    }


    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(worldObj, this.pos);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0,1,2,3,4,5,6,7,8};
    }

    public boolean isPowerExpander() {
        return powerExpander;
    }
}
