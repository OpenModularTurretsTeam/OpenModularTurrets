package omtteam.openmodularturrets.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.api.tileentity.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nonnull;

import static omtteam.openmodularturrets.turret.TurretHeadUtil.getTurretBaseFacing;

/**
 * Created by Keridos on 25/11/17.
 * This Class
 */
public class BaseAddon extends TileEntityBase implements ITurretBaseAddonTileEntity, ITickable {
    private EnumFacing orientation;

    public BaseAddon() {
        this.orientation = EnumFacing.NORTH;
    }

    public EnumFacing getOrientation() {
        return orientation;
    }

    private void setOrientation(EnumFacing orientation) {
        this.orientation = orientation;
    }

    @Override
    public Player getOwner() {
        return getBase().getOwner();
    }

    public TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(this.getWorld(), this.pos);
    }

    public void setSide() {
        this.setOrientation(getTurretBaseFacing(this.getWorld(), this.pos));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setByte("direction", (byte) orientation.ordinal());
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("direction")) {
            this.setOrientation(EnumFacing.getFront(nbtTagCompound.getByte("direction")));
        }
    }

    @Override
    public void update() {
        if (this.getWorld().getWorldTime() % 15 == 0 && getBase() == null) {
            this.getWorld().destroyBlock(this.pos, true);
        }
    }

    @Nonnull
    @Override
    public TileEntityOwnedBlock getLinkedBlock() {
        return getBase();
    }
}
