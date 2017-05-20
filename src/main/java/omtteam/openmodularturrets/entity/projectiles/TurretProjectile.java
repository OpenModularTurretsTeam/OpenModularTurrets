package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.omlib.util.PlayerUtil;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public abstract class TurretProjectile extends EntityThrowable {
    public float gravity;
    public boolean isAmped;
    public int amp_level;
    ItemStack ammo;
    private TurretBase turretBase;

    TurretProjectile(World world) {
        super(world);
    }

    TurretProjectile(World world, TurretBase turretBase) {
        super(world);
        this.turretBase = turretBase;
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
    }

    TurretProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world);
        this.ammo = ammo;
        this.turretBase = turretBase;
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
    }

    @SuppressWarnings("ConstantConditions")
    boolean canDamagePlayer(EntityPlayer entityPlayer) {
        if (!ConfigHandler.turretDamageTrustedPlayers) {
            if (this.turretBase.getTrustedPlayer(entityPlayer.getUniqueID()) != null || PlayerUtil.getPlayerUIDUnstable(
                    this.turretBase.getOwner()).equals(entityPlayer.getUniqueID())) {
                return false;
            }
        }
        return true;
    }

    public abstract void onHitBlock(IBlockState block, BlockPos pos);

    public abstract void onHitEntity(Entity entity);

    @Override
    public void onEntityUpdate() {
        if (ticksExisted >= 80) {
            this.setDead();
        }

        Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceResultBlock = this.worldObj.rayTraceBlocks(vec3d1, vec3d, false, true, false);

        if (raytraceResultBlock != null) {
            onHitBlock(this.worldObj.getBlockState(raytraceResultBlock.getBlockPos()), raytraceResultBlock.getBlockPos());
            return;
        }

        Entity entity = null;

        List<Entity> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(1.0D));
        double d0 = 0.0D;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = list.get(i);

            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
            RayTraceResult raytraceResultEntity = axisalignedbb.calculateIntercept(vec3d1, vec3d);

            if (raytraceResultEntity != null) {
                double d1 = vec3d1.squareDistanceTo(raytraceResultEntity.hitVec);

                if (d1 < d0 || d0 == 0.0D) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        if (entity != null) {
            onHitEntity(entity);
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    public boolean writeToNBTOptional(NBTTagCompound nbtTagCompound) {
        this.setDead();
        return false;
    }

    protected void setMobDropLoot(Entity entity) {
        EntityLivingBase entityLivingBase;
        if (entity instanceof EntityLivingBase) {
            entityLivingBase = (EntityLivingBase) entity;
            if (!(entityLivingBase instanceof EntityPlayer) && !entityLivingBase.getTags().contains("openmodularturrets:turretHit")) {
                entityLivingBase.addTag("openmodularturrets:turretHit");
            }
        }
    }
}
