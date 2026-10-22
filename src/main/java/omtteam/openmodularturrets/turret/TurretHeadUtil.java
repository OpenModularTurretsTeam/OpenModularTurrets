package omtteam.openmodularturrets.turret;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.omlib.util.world.Pos;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import java.util.HashMap;
import java.util.Map;


/**
 * World helpers for turrets: finding the base, aiming and the model rotation. The game rules that lived here in 3.x
 * (ammo, upgrades, addons, warnings) are in the core {@code TurretBaseLogic}.
 */
public class TurretHeadUtil {
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

    public static Map<EnumFacing, TurretTileEntity> getBaseTurrets(World world, BlockPos pos) {
        Map<EnumFacing, TurretTileEntity> map = new HashMap<>();
        if (world == null) {
            return map;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretTileEntity) {
                map.put(facing, (TurretTileEntity) world.getTileEntity(offsetPos));
            }
        }

        return map;
    }

    public static float getAimYaw(Entity target, Pos pos) {
        Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);

        double dX = targetPos.x - (pos.getX() + 0.5F);
        double dZ = targetPos.z - (pos.getZ() + 0.5F);


        float yaw = (float) ((Math.atan2(dX, dZ)));
        if (yaw < 0) {
            yaw += 2 * Math.PI;
        }

        return yaw / (float) Math.PI * 180F;
    }

    public static float getAimPitch(Entity target, Pos pos) {
        Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);

        double dX = (targetPos.x - 0.5F) - (pos.getX() + 0.5F);
        double dY = (targetPos.y + target.getEyeHeight()) - (pos.getY() + 0.5F);
        double dZ = (targetPos.z - 0.5F) - (pos.getZ() + 0.5F);

        double distance = Math.sqrt(dZ * dZ + dX * dX);

        float pitch = (float) Math.atan2(dY, distance);
        if (pitch < 0) {
            pitch += 2 * Math.PI;
        }
        // Convert it to base rotation dependent values
        return pitch / (float) Math.PI * 180F;
    }
}
