package omtteam.openmodularturrets.tileentity.turret;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;
import java.util.List;

import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntitiesByClass;

/**
 * World helpers for turrets: finding the base of a turret and the turrets of a base (1.20.1+ {@code TurretMount};
 * {@code turret.TurretHeadUtil} until 4.0.0). The game rules that lived here in 3.x (ammo, upgrades, addons, warnings) are in the core
 * {@code TurretBaseLogic}.
 */
public class TurretMount {
    public static TurretBase getTurretBase(World world, BlockPos pos) {
        if (world == null) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(offsetPos);
            }
        }

        return null;
    }

    public static EnumFacing getTurretBaseFacing(World world, BlockPos pos) {
        if (world == null) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretBase) {
                return facing;
            }
        }

        return null;
    }

    /** The turrets attached to a base, on any of its six faces. */
    public static List<TurretTileEntity> getTurrets(TurretBase base) {
        return getTouchingTileEntitiesByClass(base.getWorld(), base.getPos(), TurretTileEntity.class);
    }

    /** The turret on one face of a base, null if there is none. */
    @Nullable
    public static TurretTileEntity getTurret(TurretBase base, EnumFacing face) {
        TileEntity tileEntity = base.getWorld().getTileEntity(base.getPos().offset(face));
        return tileEntity instanceof TurretTileEntity ? (TurretTileEntity) tileEntity : null;
    }
}
