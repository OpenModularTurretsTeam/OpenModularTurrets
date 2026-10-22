package omtteam.openmodularturrets.turret;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.omlib.util.world.Pos;
import omtteam.omlib.util.world.WorldUtil;
import omtteam.openmodularturrets.blocks.BlockBaseAttachment;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static omtteam.openmodularturrets.blocks.BlockBaseAttachment.BASE_ADDON_META;

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

    public static Map<EnumFacing, TurretHead> getBaseTurrets(World world, BlockPos pos) {
        Map<EnumFacing, TurretHead> map = new HashMap<>();
        if (world == null) {
            return map;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretHead) {
                map.put(facing, (TurretHead) world.getTileEntity(offsetPos));
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

    public static boolean baseHasNoLootDeleter(TurretBase base) {
        List<IBlockState> states = WorldUtil.getTouchingBlockStates(base.getWorld(), base.getPos());
        for (IBlockState state : states) {
            if (state.getBlock() instanceof BlockBaseAttachment) {
                if (state.getValue(BASE_ADDON_META) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static float getRotationXYFromTurretHead(AbstractDirectedTurret turretHead) {
        float yaw = turretHead.getYaw();
        float pitch = turretHead.getPitch();
        float rotationXY = 0F;
        if (turretHead != null) {
            switch (turretHead.baseFacing) {
                case UP:
                    rotationXY = ((-pitch) * 0.017453292F);
                    break;
                case DOWN:
                    rotationXY = ((-pitch) * 0.017453292F);
                    break;
                case EAST:
                    if (yaw > 270F) {
                        rotationXY = ((yaw) * 0.017453292F);
                    } else {
                        rotationXY = ((-yaw - 180F) * 0.017453292F);
                    }
                    break;
                case WEST:
                    if (yaw < 90F) {
                        rotationXY = ((-yaw) * 0.017453292F);
                    } else {
                        rotationXY = ((yaw + 180F) * 0.017453292F);
                    }
                    break;
                case NORTH:
                    if (yaw > 180F) {
                        rotationXY = ((-yaw - 90F) * 0.017453292F);
                    } else {
                        rotationXY = ((yaw - 90f) * 0.017453292F);
                    }
                    break;
                case SOUTH:
                    if (yaw < 180F) {
                        rotationXY = ((-yaw + 90F) * 0.017453292F);
                    } else {
                        rotationXY = ((yaw + 90F) * 0.017453292F);
                    }
                    break;
            }
        }
        return rotationXY;
    }

    // Pitch of the Weapon
    public static float getRotationXZFromTurretHead(AbstractDirectedTurret turretHead) {
        float yaw = turretHead.getYaw();
        float pitch = turretHead.getPitch();
        float rotationXZ = 0F;
        if (turretHead != null) {
            switch (turretHead.baseFacing) {
                case UP:
                    rotationXZ = ((yaw + 180F) * 0.017453292F);
                    break;
                case DOWN:
                    rotationXZ = (-yaw * 0.017453292F);
                    break;
                case EAST:
                    if (yaw > 270F) {
                        rotationXZ = ((-pitch - 90F) * 0.017453292F);
                    } else if (yaw < 90F) {
                        rotationXZ = ((pitch - 90F) * 0.017453292F);
                    } else {
                        rotationXZ = ((pitch + 90F) * 0.017453292F);
                    }
                    break;
                case WEST:
                    if (yaw < 90F) {
                        rotationXZ = ((pitch + 90F) * 0.017453292F);
                    } else {
                        rotationXZ = ((-pitch - 90F) * 0.017453292F);
                    }
                    break;
                case NORTH:
                    if (yaw < 180F) {
                        rotationXZ = ((-pitch - 90F) * 0.017453292F);
                    } else {
                        rotationXZ = ((pitch + 90F) * 0.017453292F);
                    }
                    break;
                case SOUTH:
                    if (yaw > 180F) {
                        rotationXZ = ((-pitch - 90F) * 0.017453292F);
                    } else {
                        rotationXZ = ((pitch + 90F) * 0.017453292F);
                    }
                    break;
            }
        }
        return rotationXZ;
    }
}
