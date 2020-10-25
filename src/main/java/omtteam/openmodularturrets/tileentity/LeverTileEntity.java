package omtteam.openmodularturrets.tileentity;

import net.minecraft.util.ITickable;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.openmodularturrets.api.tileentity.ITurretBaseAddonTileEntity;

import javax.annotation.Nonnull;

public class LeverTileEntity extends TileEntityBase implements ITickable, ITurretBaseAddonTileEntity {
    public float rotation = 0;
    public boolean isTurning = false;
    private TurretBase base;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void update() {
        if (base == null) {
            if (this.getWorld().getTileEntity(this.pos.east()) instanceof TurretBase && ((TurretBase) this.getWorld().getTileEntity(this.pos.east())).getTier() == 1) {
                this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.east());
            }

            if (this.getWorld().getTileEntity(this.pos.west()) instanceof TurretBase && ((TurretBase) this.getWorld().getTileEntity(this.pos.west())).getTier() == 1) {
                this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.west());
            }

            if (this.getWorld().getTileEntity(this.pos.south()) instanceof TurretBase && ((TurretBase) this.getWorld().getTileEntity(this.pos.south())).getTier() == 1) {
                this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.south());
            }

            if (this.getWorld().getTileEntity(this.pos.north()) instanceof TurretBase && ((TurretBase) this.getWorld().getTileEntity(this.pos.north())).getTier() == 1) {
                this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.north());
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

    @Nonnull
    @Override
    public TileEntityOwnedBlock getLinkedBlock() {
        return base;
    }
}
