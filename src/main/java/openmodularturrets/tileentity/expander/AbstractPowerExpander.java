package openmodularturrets.tileentity.expander;

import net.minecraft.util.ITickable;
import openmodularturrets.tileentity.TileEntityOMT;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.TurretHeadUtil;

public abstract class AbstractPowerExpander extends TileEntityOMT implements ITickable{
    public float baseFitRotationX;
    public float baseFitRotationZ;
    private boolean hasSetSide = false;
    private int tier;

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

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    private TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(worldObj, this.pos);
    }
}
