package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.omlib.util.PlayerUtil;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.PlayerUtil.isPlayerOwner;
import static omtteam.omlib.util.PlayerUtil.isPlayerTrusted;

public abstract class TurretProjectile extends EntityThrowable {
    public float gravity;
    public boolean isAmped;
    public int amp_level;
    ItemStack ammo;
    protected TurretBase turretBase;
    public int framesRendered = 0;
    public int fakeDrops;

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
        fakeDrops = TurretHeadUtil.getFakeDropsLevel(turretBase);
    }

    TurretProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world);
        this.ammo = ammo;
        this.turretBase = turretBase;
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
        fakeDrops = TurretHeadUtil.getFakeDropsLevel(turretBase);
    }

    boolean canDamagePlayer(EntityPlayer entityPlayer) {
        if (entityPlayer != null && !entityPlayer.getEntityWorld().isRemote) {
            if (!ConfigHandler.turretDamageTrustedPlayers) {
                if (PlayerUtil.isPlayerTrusted(entityPlayer, this.turretBase)) {
                    return false;
                }
            }
            if (PlayerUtil.isPlayerOwner(entityPlayer, this.turretBase)) {
                return false;
            }
        }
        return true;
    }

    boolean canDamageEntity(Entity entity) {
        if (entity != null && !getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {
            if (entity instanceof EntityTameable) {
                EntityLivingBase entityOwner = ((EntityTameable) entity).getOwner();
                if (entityOwner != null && entityOwner instanceof EntityPlayer) {
                    EntityPlayer owner = (EntityPlayer) entityOwner;
                    if (isPlayerOwner(owner, turretBase) || isPlayerTrusted(owner, turretBase)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public abstract void onHitBlock(IBlockState block, BlockPos pos);

    public abstract void onHitEntity(Entity entity);

    public abstract void playSound();

    public abstract double getDamageAmpBonus();

    @Override
    public void onUpdate() {

        if (this.ticksExisted > 80) {
            this.setDead();
        }

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.worldObj.rayTraceBlocks(vec3d, vec3d1);

        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(0.2D));

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                this.onHitEntity(entity);
            }
        }

        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                this.onHitBlock(this.worldObj.getBlockState(raytraceresult.getBlockPos()), raytraceresult.getBlockPos());
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            //TODO: what is this?
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = this.getGravityVelocity();

        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }
            f1 = 0.8F;
        }

        this.motionX *= (double) f1;
        this.motionY *= (double) f1;
        this.motionZ *= (double) f1;

        if (!this.hasNoGravity()) {
            this.motionY -= (double) f2;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
    }


    @Override
    @ParametersAreNonnullByDefault
    public boolean writeToNBTOptional(NBTTagCompound nbtTagCompound) {
        this.setDead();
        return false;
    }

    protected void setTagsForTurretHit(Entity entity) {
        EntityLivingBase entityLivingBase;
        if (entity instanceof EntityLivingBase) {
            entityLivingBase = (EntityLivingBase) entity;
            if (!(entityLivingBase instanceof EntityPlayer) && !entityLivingBase.getTags().contains("openmodularturrets:turret_hit")) {
                entityLivingBase.addTag("openmodularturrets:turret_hit");
            }
            if (!(entityLivingBase instanceof EntityPlayer) && this.fakeDrops > -1) {
                entityLivingBase.addTag("openmodularturrets:fake_drops_" + this.fakeDrops);
            }
        }
    }

    public int getFramesRendered() {
        return framesRendered;
    }

    public void setFramesRendered(int framesRendered) {
        this.framesRendered = framesRendered;
    }


}
