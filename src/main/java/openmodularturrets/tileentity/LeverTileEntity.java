package openmodularturrets.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class LeverTileEntity extends TileEntity implements ITickable {
    public float rotation = 0;
    public boolean isTurning = false;
    private TurretBase base;

    @Override
    public void update() {
        if (base == null) {
            if (worldObj.getTileEntity(this.pos.east()) instanceof TurretBase && ((TurretBase)worldObj.getTileEntity(this.pos.east())).getTier() == 1) {
                this.base = (TurretBase) worldObj.getTileEntity(this.pos.east());
            }

            if (worldObj.getTileEntity(this.pos.west()) instanceof TurretBase && ((TurretBase)worldObj.getTileEntity(this.pos.east())).getTier() == 1) {
                this.base = (TurretBase) worldObj.getTileEntity(this.pos.west());
            }

            if (worldObj.getTileEntity(this.pos.south()) instanceof TurretBase && ((TurretBase)worldObj.getTileEntity(this.pos.east())).getTier() == 1) {
                this.base = (TurretBase) worldObj.getTileEntity(this.pos.south());
            }

            if (worldObj.getTileEntity(this.pos.north()) instanceof TurretBase && ((TurretBase)worldObj.getTileEntity(this.pos.east())).getTier() == 1) {
                this.base = (TurretBase) worldObj.getTileEntity(this.pos.north());
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
}
